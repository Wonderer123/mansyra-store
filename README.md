<div align="center">

<img src="static/images/mansyra.jpg" alt="Mansyra Beauty logo" width="260">

# Mansyra

**Beauty & Cosmetics E-commerce · Beta**

A full-stack shopping site for a fictional Indian beauty brand, built from scratch with
vanilla HTML/CSS/JS on the front and a tiny, framework-free Java server on the back.

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Frontend](https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JS-blue?logo=javascript&logoColor=white)
![Storage](https://img.shields.io/badge/Storage-JSON%20files-lightgrey?logo=json&logoColor=white)
![Deploy](https://img.shields.io/badge/Deploy-Docker%20%7C%20Render-46E3B7?logo=docker&logoColor=white)
![Status](https://img.shields.io/badge/Status-Beta-yellow)

</div>

---

## ✨ Features

| | |
|---|---|
| 🛍️ **Storefront** | Hero banners plus curated sections: Bestsellers, Face, Perfume, Lips, Eye, Trending, Skin Care, New Arrivals and Must Try |
| 🔐 **Accounts** | Register with name, email, phone and password. Log in with **email or phone**. Session token kept in `localStorage` |
| 🛒 **Cart** | Add from any product card with a fly-to-cart animation and live badge count. Stored locally **and** synced to the server |
| 🎟️ **Coupons** | `GLOW40` · `BEAUTY200` · `BUY2GET1` |
| 💳 **Checkout** | Delivery address, 18 % tax, flat ₹100 delivery, Cash-on-Delivery |
| 📦 **Order history** | Every order the logged-in user has placed, with items and totals |
| 💬 **Help & Support** | FAQ, shipping, returns and privacy |

---

## 🚀 Quick start

> Requires a **JDK 17 or newer**. Check with `java -version`.

**Windows** — double-click `start.bat`, or:

```bat
javac -cp .;json-20240303.jar app\*.java
java  -cp .;json-20240303.jar app.Main
```

**macOS / Linux**:

```sh
javac -cp .:json-20240303.jar app/*.java
java  -cp .:json-20240303.jar app.Main
```

Open **http://localhost:8080** and sign in with the demo account:

```
email:    mansi@mansyra.com   (or phone 1234567890)
password: mansi@123
```

> ⚠️ Start the server **from this folder**. It looks for `static/` and the `*.json` files
> relative to the working directory.

---

## 🌐 Deploying

This is **not a static site**. Login, cart and orders all need the Java server, so
Netlify, GitHub Pages and Vercel static hosting will not work. Use a host that runs a
container. The included `Dockerfile` works unchanged on:

| Host | How |
|---|---|
| **Render** (recommended, free tier) | New → Web Service → connect this repo → environment **Docker**. A `render.yaml` is included, so **Blueprint** deploys also work. |
| **Railway / Fly.io / Koyeb** | Connect the repo; the `Dockerfile` is detected automatically. |

The server reads the `PORT` environment variable that these hosts set, and falls back to
`8080` locally.

> 📝 **Data persistence:** users, carts and orders live in JSON files on disk. Free tiers wipe
> the disk on every redeploy or restart, so accounts and orders reset. Fine for a demo; a
> real deployment needs a database.

---

## 🧱 Tech stack

| Layer | Details |
|---|---|
| Frontend | Static HTML, CSS and vanilla JavaScript in `static/` |
| Backend | Java, using the JDK's built-in `com.sun.net.httpserver.HttpServer` |
| JSON | `org.json` (`json-20240303.jar`, bundled) |
| Storage | Flat files: `newusers.json`, `cart.json`, `orders.json` |

---

## 🗂️ Project structure

```
Mansyra_beta/
├── app/                       Java backend
│   ├── Main.java              Starts the server and wires the routes
│   ├── StaticFileHandler.java Serves static/ ; "/" → goof.html
│   ├── UserController.java    /api/register · /api/login · /api/logout
│   ├── CartController.java    /api/cart (get / add / remove / clear)
│   ├── OrderController.java   /api/orders (save / list)
│   ├── PaymentController.java /api/checkout
│   ├── SessionManager.java    In-memory token → user map
│   └── DataStore.java         Reads and writes the JSON files
├── static/                    Frontend
│   ├── goof.html              Home page / storefront
│   ├── login.html             Register and login
│   ├── cart.html              Cart
│   ├── checkout.html          Checkout and coupons
│   ├── orders.html            Order history
│   ├── help_and_support.html  FAQ, shipping, returns, privacy
│   ├── css/  js/  images/
├── newusers.json              Registered users
├── cart.json                  Carts, keyed by user
├── orders.json                Orders, keyed by user
├── json-20240303.jar          org.json library
├── start.bat                  One-click launcher (Windows)
├── Dockerfile · render.yaml   Hosting
└── README.md
```

---

## 🔌 API

<details>
<summary>Click to expand the endpoint reference</summary>

<br>

| Method | Path | Body / Query | Returns |
|---|---|---|---|
| `POST` | `/api/register` | `{name, email, phone, password}` | text message |
| `POST` | `/api/login` | `{user, password}` — email or phone | `{status, token, user}` |
| `POST` | `/api/logout` | `{token}` | `"logged out"` |
| `GET` | `/api/cart` | `?user=NAME` | `{items, subtotal, tax, delivery, total}` |
| `POST` | `/api/cart` | `{user, item}` | updated cart array |
| `POST` | `/api/cart/remove` | `{user, item}` | `"removed"` |
| `POST` | `/api/cart/clear` | `{user}` | `"cleared"` |
| `POST` | `/api/orders` | `{user, order}` | `"saved"` |
| `GET` | `/api/orders` | `?user=NAME` | array of orders |
| `POST` | `/api/checkout` | `{user}` | `"Payment Successful"` |

Login `status` is one of `success`, `wrong_password` or `user_not_found`.

</details>

---

## ⚠️ Known limitations

This is a learning project in beta. Things a production build would need to fix:

- Passwords are stored and sent in plain text.
- Sessions are in memory only and vanish on restart. API routes do not verify the token; they trust the `user` name the browser sends.
- No server-side input validation, and concurrent writes to the JSON files are not guarded.
- Cash-on-Delivery only. `/api/checkout` does not process real payments.
- The "View All" links on the home page are placeholders.

---

<div align="center">

Made with 💄 for Mansyra Beauty

</div>
