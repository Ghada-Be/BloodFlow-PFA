import { useEffect, useRef } from "react";
import axios from "axios";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { createNotificationConnection } from "../realtime/notificationConnection";

const MS3_URL = import.meta.env.VITE_SUPERVISION_API_URL || "http://localhost:8083";

export default function NotificationRealtimeProvider({ children }) {
  const connectionRef = useRef(null);
  const shownNotifications = useRef(new Set());

  useEffect(() => {
    const token = localStorage.getItem("token");
    const user = JSON.parse(localStorage.getItem("user") || "null");

    if (!token || !user?.id) return;

    const connection = createNotificationConnection(token);
    connectionRef.current = connection;

    connection.on("ReceiveNotification", async (notification) => {
      if (!notification?.id || shownNotifications.current.has(notification.id)) return;

      shownNotifications.current.add(notification.id);

      const priority = String(notification.priority || "normal").toLowerCase();
      const message = notification.message || notification.title || "Nouvelle notification BloodFlow";

      const toastOptions = {
        position: "top-right",
        autoClose: priority === "critical" || priority === "high" ? 9000 : 5000,
        closeOnClick: true,
        pauseOnHover: true,
      };

      if (priority === "critical" || priority === "high") {
        toast.error(message, toastOptions);
      } else if (priority === "warning" || priority === "medium") {
        toast.warning(message, toastOptions);
      } else {
        toast.info(message, toastOptions);
      }

      try {
        await axios.patch(`${MS3_URL}/api/notifications/${notification.id}/read`, {}, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } catch (error) {
        console.warn("Impossible de marquer la notification comme lue", error);
      }
    });

    connection
      .start()
      .then(async () => {
        console.log("SignalR MS3 connected");
        await connection.invoke("JoinUserGroup", String(user.id));

        if (Array.isArray(user.roles)) {
          for (const role of user.roles) {
            await connection.invoke("JoinRoleGroup", role);
          }
        }
      })
      .catch((error) => console.error("SignalR MS3 connection error", error));

    return () => {
      connection.stop();
    };
  }, []);

  return (
    <>
      {children}
      <ToastContainer />
    </>
  );
}
