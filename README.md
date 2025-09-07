
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Android Build](https://img.shields.io/badge/Android-Build%20Passing-brightgreen)](#)

SafeLugg is a secure, convenient, and reliable luggage storage and management platform designed for travelers and storage partners. It allows travelers to store luggage safely at nearby partner locations while vendors can efficiently manage storage requests and notifications.

---

## Table of Contents
- [Client App](#client-app)
  - [Features](#features)
  - [Technology Stack](#technology-stack)
- [Vendor App](#vendor-app)
  - [Features](#features-1)
  - [Technology Stack](#technology-stack-1)
- [Contribution](#contribution)
- [License](#license)
- [Screenshots](#screenshots)
- [How It Works](#how-it-works)

---

## Client App

The **SafeLugg Client App** is for travelers who need secure, short-term luggage storage during trips.

### Features
- Search for nearby luggage storage locations.
- Book storage slots and pay seamlessly.
- Track luggage status in real-time.
- Receive push notifications for booking updates.
- Search and filter storage locations by category and distance.

### Technology Stack
| Layer | Technology | Logo |
|-------|------------|------|
| Frontend | Android Jetpack Compose | ![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-orange?logo=jetbrains&logoColor=white) |
| Authentication | Firebase Auth (Google Sign-In) | ![Firebase](https://img.shields.io/badge/Firebase-Auth-yellow?logo=firebase&logoColor=white) |
| Notifications | Firebase Cloud Messaging | ![FCM](https://img.shields.io/badge/FCM-Notifications-blue?logo=firebase&logoColor=white) |
| Backend API | Spring Boot | ![Spring Boot](https://img.shields.io/badge/Spring-Boot-green?logo=spring&logoColor=white) |
| Database | PostgreSQL | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-DB-blue?logo=postgresql&logoColor=white) |

---

## Vendor App

The **SafeLugg Vendor App** is designed for storage partners to efficiently manage traveler bookings and storage requests.

### Features
- Register and verify storage locations.
- Manage storage requests and bookings in real-time.
- Receive push notifications for new bookings.
- Track vendor verification status.
- Update storage availability and pricing dynamically.

### Technology Stack
| Layer | Technology | Logo |
|-------|------------|------|
| Frontend | Android Jetpack Compose | ![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-orange?logo=jetbrains&logoColor=white) |
| Authentication | Firebase Auth | ![Firebase](https://img.shields.io/badge/Firebase-Auth-yellow?logo=firebase&logoColor=white) |
| Notifications | Firebase Cloud Messaging | ![FCM](https://img.shields.io/badge/FCM-Notifications-blue?logo=firebase&logoColor=white) |
| Backend API | Spring Boot | ![Spring Boot](https://img.shields.io/badge/Spring-Boot-green?logo=spring&logoColor=white) |
| Database | PostgreSQL | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-DB-blue?logo=postgresql&logoColor=white) |

---

## Contribution
Contributions are welcome! Please fork the repository and submit a pull request with a detailed description of your changes.

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Screenshots

### Client App
![Client Home](https://raw.githubusercontent.com/your-username/safelugg/main/assets/client_home.png)
![Client Booking](https://raw.githubusercontent.com/your-username/safelugg/main/assets/client_booking.png)

### Vendor App
![Vendor Dashboard](https://raw.githubusercontent.com/your-username/safelugg/main/assets/vendor_dashboard.png)
![Vendor Requests](https://raw.githubusercontent.com/your-username/safelugg/main/assets/vendor_requests.png)

---

## How It Works

```mermaid
graph TD
A[Traveler searches for storage] --> B[Selects location and books slot]
B --> C[Vendor receives booking request]
C --> D[Vendor verifies and confirms storage]
D --> E[Traveler drops off luggage]
E --> F[Vendor updates status and notifies traveler]
