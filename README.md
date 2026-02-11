This is a comprehensive and 
Project Title: Android Storage Cleaner & File Manager (Offline, Background Capable)
Role: You are an expert Senior Android Developer specializing in Kotlin, Jetpack Compose, File I/O, and memory optimization.
Objective: Create a native Android application that allows users to select any folder on their device, filter files by date range and type, analyze them in the background (handling massive file counts without crashing), and perform bulk deletions. The app must be robust, offline-first, and handle permission scopes for Android 11+ (All Files Access).
1. Technical Stack & Architecture
 * Language: Kotlin.
 * UI Framework: Jetpack Compose (Material 3).
 * Architecture: MVVM (Model-View-ViewModel).
 * Concurrency: Kotlin Coroutines & Flow.
 * Background Processing: Foreground Service (Critical: Must run even if the user minimizes the app during analysis).
 * Image Loading: Coil (optimized for scrolling performance).
 * Permissions: MANAGE_EXTERNAL_STORAGE (Android 11+) to ensure access to WhatsApp/System folders.
2. Data Models
Create a comprehensive data class FileItem:
 * path: String (Absolute path)
 * name: String
 * size: Long (in bytes)
 * creationDate: Long (Timestamp)
 * mimeType: String (to categorize as Video, Audio, Doc, etc.)
 * isSelected: Boolean (MutableState for UI selection)
3. Screen Specifications & Flows
Screen 1: Configuration & Input
UI Elements:
 * Folder Selector: A button "Select Folder to Clean". Launches the system document tree picker or a custom directory picker.
 * Path Display: Text showing the currently selected path (e.g., /storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media).
 * Date Range Picker: Two inputs: "Start Date" and "End Date". Defaults to "All Time".
 * Sort Options: Radio buttons or Dropdown:
   * Largest to Smallest
   * Smallest to Largest
   * Newest to Oldest
 * Action Button: "Start Analysis".
Logic:
 * Request MANAGE_EXTERNAL_STORAGE permission immediately on launch if not granted.
 * Pass the selected folder path, date range, and sort preference to the Analysis Service.
Screen 2: Analysis (Loading State)
UI Elements:
 * Progress Indicator: Linear Progress Indicator (determinate).
 * Status Text: "Analyzing files... Found [X] files."
 * Background Info: Text explaining "You can minimize this app. We will notify you when done."
Logic:
 * Foreground Service: Start a Service with a persistent Notification.
 * Recursive Scanning: Recursively scan the selected folder.
 * Filtering: specific files are kept in memory only if they match the Date Range.
 * Performance: Do NOT load bitmaps/thumbnails here. Only metadata (path, size, date). Use Sequence or Flow to handle large lists (20k+ files) to prevent OOM (Out of Memory) errors.
 * Navigation: Automatically navigate to Screen 3 when the list is ready and sorted.
Screen 3: Results & Cleaning (The Dashboard)
UI Layout:
 * Top Bar (Sticky/Fixed):
   * Selection Stats: "Selected: [X] files".
   * Size Stats: "Total Size: [Y] MB/GB" (Calculated dynamically).
   * Delete Button: Active only when Selection > 0.
 * Main Content: LazyColumn (Scrollable list).
List Item Design (Row):
 * Checkbox: Left aligned.
 * Thumbnail: Small icon based on file type (or thumbnail for images/video).
 * File Details: Filename (truncated), Date, Size (formatted readable).
 * Action Buttons (Right side):
   * "i" Icon: Shows a popup dialog with Full Path, Full Name, Exact Size.
   * "Open" Icon: Launches an Intent to open the file in the default external viewer.
   * "Trash" Icon: Individual immediate delete button (Red).
Logic:
 * Multi-Select: Tapping a checkbox updates the isSelected state.
 * Dynamic Calculation: Instantly recalculate the "Total Size" header when checkboxes change.
 * Batch Delete:
   * When "Delete" in the header is clicked, show an Alert Dialog: "Are you sure you want to delete [X] files totaling [Y] MB?"
   * On confirmation, perform deletion in a Coroutine (IO Dispatcher).
   * Update the UI list to remove deleted items.
   * Show a Toast/Snackbar: "Deleted [X] files."
4. Critical Performance Requirements
 * Memory Safety: The app must handle a folder containing 20,000 to 50,000 files without crashing. Use Pagination or efficient LazyColumn state management. Do not hold strong references to Bitmaps.
 * Concurrency: File scanning must happen on Dispatchers.IO. Sorting must happen on Dispatchers.Default.
 * Service Lifecycle: If the user minimizes the app while it is scanning 20,000 files, the Foreground Service must keep the process alive and post a notification updating the count (e.g., "Scanned 5000/20000").
5. Task for the AI
Please write the code for this application. Start by setting up the AndroidManifest.xml (permissions), the FileItem data class, and the FileScannerService (the background logic). Then, implement the ViewModel and the Jetpack Compose UI for the Results Screen.
--- END OF PROMPT ---
