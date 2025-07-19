# 📱 Mention — Social Insight App (Kotlin Multiplatform)

**Mention** is a modern and minimal cross-platform social insight app built with **Kotlin Multiplatform**. It helps Twitter/X users analyze their interactions, discover their top mutuals, identify "silent fans", and better understand their social circle through data-driven insights — all while respecting privacy.

![App Screenshots](images/screenshots.png)

---

## 🚀 Features

- 🎯 **Top 25 Mutuals** – See who interacts with you the most among your mutual followers
- 👀 **Silent Fans** – Detect users who like or retweet often but never reply or mention you
- ❤️ **Top Likers & Retweeters** – Discover who engages with your posts the most
- 🔄 **Follow/Unfollow Insights** – Track one-way followers and mutuals
- 📊 **Interaction Scoring** – Each user gets a 0–100 interaction score
- 🔐 100% Based on Public Data – No privacy violations or fake “profile viewers”

---

## 🛠 Technologies Used

**Mention** is built using the latest multiplatform technologies to support both Android and iOS from a single codebase.

| Layer | Technology |
|-------|------------|
| UI | [JetBrains Compose Multiplatform](https://jb.gg/compose) |
| Navigation | [Compose Navigation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html) |
| Networking | [Ktor](https://ktor.io/) |
| Serialization | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) |
| Image Loading | [Coil](https://github.com/coil-kt/coil) |
| Dependency Injection | [Koin](https://github.com/InsertKoinIO/koin) |

---

## ⚙️ Architecture

The project follows **Clean Architecture** principles with shared business logic and data models across platforms.

```bash
mention/
├── shared/           # Shared business logic (Kotlin Multiplatform)
│   ├── data/
│   ├── domain/
│   └── presentation/
├── androidApp/       # Android-specific launcher and platform logic
├── iosApp/           # iOS-specific launcher (via Kotlin Native)