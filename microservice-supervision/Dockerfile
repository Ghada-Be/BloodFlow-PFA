FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

COPY BloodFlow.MS3.csproj ./
RUN dotnet restore BloodFlow.MS3.csproj

COPY . ./
RUN dotnet publish BloodFlow.MS3.csproj -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
COPY --from=build /app/publish ./

EXPOSE 8083
ENV ASPNETCORE_URLS=http://+:8083
ENTRYPOINT ["dotnet", "BloodFlow.MS3.dll"]
