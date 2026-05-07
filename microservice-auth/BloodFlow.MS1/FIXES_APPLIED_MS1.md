# BloodFlow MS1 Auth Service — Fixes Applied

This version fixes the 5 items requested for the authentication microservice.

## 1. Account lock after 3 failed login attempts

Files changed:

- `src/main/java/com/bloodflow/auth/service/AuthService.java`
- `src/main/java/com/bloodflow/auth/exception/AccountLockedException.java`
- `src/main/java/com/bloodflow/auth/exception/GlobalExceptionHandler.java`
- `src/main/java/com/bloodflow/auth/security/CustomUserDetailsService.java`
- `src/main/java/com/bloodflow/auth/security/UserPrincipal.java`

Behavior:

- Wrong password increments `failedLoginAttempts`.
- After 3 wrong attempts, `lockoutUntil` is set for 15 minutes.
- API returns HTTP `423 LOCKED` with a clear message.
- Successful login resets `failedLoginAttempts` and `lockoutUntil`.
- A locked account cannot use an old access token during the lockout period.

## 2. Revoke tokens when admin disables an account

File changed:

- `src/main/java/com/bloodflow/auth/service/AdminUserService.java`

Behavior:

- `PATCH /api/admin/users/{id}/disable` now:
  - sets status to `DISABLED`
  - resets failed attempts
  - clears lockout
  - revokes all refresh tokens for the user
- Old JWT access tokens are blocked because `CustomUserDetailsService` refuses disabled users.

## 3. Prevent removing the last user role

File changed:

- `src/main/java/com/bloodflow/auth/service/AdminUserService.java`

Behavior:

- `DELETE /api/admin/users/{id}/roles/{roleName}` now refuses the action if the user has only one role.
- It also returns a clean error if the user does not actually have that role.

## 4. Clear mustChangePassword handling

Files changed:

- `src/main/java/com/bloodflow/auth/dto/response/AuthResponse.java`
- `src/main/java/com/bloodflow/auth/service/AuthService.java`

Behavior:

After login and refresh, the backend returns:

```json
{
  "mustChangePassword": true,
  "nextAction": "CHANGE_PASSWORD",
  "redirectTo": "/change-password"
}
```

If no password change is required, it returns a dashboard path according to the role, for example:

```json
{
  "mustChangePassword": false,
  "nextAction": "GO_TO_DASHBOARD",
  "redirectTo": "/dashboard/admin"
}
```

Frontend example:

```js
const result = response.data.data;
localStorage.setItem("token", result.accessToken || result.token);
localStorage.setItem("refreshToken", result.refreshToken);
localStorage.setItem("user", JSON.stringify(result.user));

navigate(result.redirectTo);
```

## 5. Real test commands

Use these from the project root folder:

```bash
mvn clean install
mvn spring-boot:run
```

Then open:

```txt
http://localhost:8081/swagger-ui.html
```

Recommended test order:

1. `GET /api/health`
2. `POST /api/auth/login`
3. Copy the `accessToken` or `token`
4. Authorize Swagger with `Bearer YOUR_TOKEN`
5. `GET /api/auth/me`
6. Test 3 wrong logins and confirm HTTP `423 LOCKED`
7. Admin disables a user and confirm the refresh token no longer works
8. Try removing the last role and confirm it is refused
9. Create staff, login with temporary password, confirm `redirectTo = /change-password`

## Important note

This ZIP was edited at code level. You still must run it on your machine with your MySQL server because database credentials differ by computer.

Check `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    username: root
    password: root
```

Change the password if your MySQL password is different.
