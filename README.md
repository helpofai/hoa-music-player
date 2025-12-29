# MyM Music - Professional Audio Player

MyM Music is a flagship-grade Android music player built with a focus on high-fidelity audio processing, modern material design, and a seamless user experience. It features a 32-bit floating-point DSP engine, a dynamic Palette-based UI, and comprehensive library management.

## 🌟 Key Features

### 🎧 Audio Engine (DSP)
*   **32-bit Floating Point Processing:**  Crystal clear audio rendering.
*   **Precision Equalizer:**  Customizable bands for detailed sound shaping.
*   **Audio Effects:**
    *   **Impact Bass:** Enhanced low-frequency response.
    *   **3D Immersion:** Spatial audio widening.
    *   **Crystal Clarity:**  High-frequency exciter.
    *   **Volume Booster:**  Clean gain amplification.

### 🎨 Modern UI/UX
*   **Material Design 3:** Fully compliant with the latest Android design guidelines.
*   **Dynamic Theming:**  UI colors adapt in real-time based on the album artwork (Palette API).
*   **Smooth Animations:**  Fluid transitions and shared element animations.
*   **Visualizers:**
    *   **Particle Visualizer:**  Reacts to beat and frequency.
    *   **Circular Spectrum:**  Modern take on classic visualizers.

### 📂 Library Management
*   **Smart Parsing:**  Automatically organizes music by Artist, Album, and Genre.
*   **Folder Browsing:**  Direct file system access for organized listening.
*   **Smart Playlists:**  Auto-generated lists like "Recently Added" and "Most Played".
*   **Search:**  Fast, indexed search for tracks and artists.

### 📱 System Integration
*   **MediaSession Support:**  Controls via notification shade, lock screen, and Bluetooth devices.
*   **Audio Focus:**  Seamless handling of interruptions (calls, notifications).
*   **Sleep Timer:**  Drift off with custom fade-out timers.

## 🛠 Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/) (100%)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles.
*   **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
*   **Async Operations:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Local Database:** [Room](https://developer.android.com/training/data-storage/room)
*   **Audio Core:** [ExoPlayer](https://exoplayer.dev/) (Media3)
*   **Image Loading:** [Coil](https://coil-kt.github.io/coil/)

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (2024.2.1) or newer.
*   JDK 17.
*   Android Device/Emulator running Android 8.0 (API 26) or higher.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/helpofai/hoa-music-player.git
    cd hoa-music-player
    ```

2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select **Open** and choose the `hoa-music-player` directory.

3.  **Sync Project:**
    *   Allow Gradle to download dependencies and sync the project.

4.  **Run:**
    *   Connect your Android device or start an emulator.
    *   Click the **Run** button (green arrow) in the toolbar.

## 📸 Screenshots

*(Placeholders - Add screenshots of Home, Now Playing, and Equalizer screens here)*

| Home Screen | Now Playing | Equalizer |
|:---:|:---:|:---:|
| ![Home](path/to/home_screenshot.png) | ![Now Playing](path/to/player_screenshot.png) | ![Equalizer](path/to/eq_screenshot.png) |

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 Contact

**HelpOfAI** - [contact@helpofai.com](mailto:contact@helpofai.com)

Project Link: [https://github.com/helpofai/hoa-music-player](https://github.com/helpofai/hoa-music-player)
