# Android Storage Cleaner - Testing Guide

This guide provides comprehensive instructions for manually testing the Android Storage Cleaner & File Manager application on a physical Android device or emulator.

## Prerequisites

### Device Requirements
- **Android Version**: Android 11 (API 30) or higher
- **Storage**: At least 100 MB free space for app installation
- **Test Data**: Recommended to have a folder with 100+ files for realistic testing

### Installation Methods

#### Method 1: Download from GitHub
1. Navigate to: https://github.com/Rishin-zartek/anshif_test
2. Download `storage-cleaner.apk` (50 MB)
3. Transfer to your Android device
4. Enable "Install from Unknown Sources" in Settings
5. Tap the APK file to install

#### Method 2: Build from Source
```bash
git clone https://github.com/Rishin-zartek/anshif_test.git
cd anshif_test
./gradlew assembleDebug
# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Test Plan

### Phase 1: Permission Flow Testing

#### Test 1.1: Initial Permission Request
**Objective**: Verify MANAGE_EXTERNAL_STORAGE permission is requested correctly

**Steps**:
1. Launch the app for the first time
2. Observe the permission dialog

**Expected Results**:
- ✅ App requests "All Files Access" permission
- ✅ Dialog explains why permission is needed
- ✅ User can grant or deny permission

**Pass/Fail**: ___________

#### Test 1.2: Permission Denial Handling
**Objective**: Verify app handles permission denial gracefully

**Steps**:
1. Launch the app
2. Deny the "All Files Access" permission
3. Try to select a folder

**Expected Results**:
- ✅ App shows error message explaining permission is required
- ✅ App provides button to open Settings
- ✅ No crash occurs

**Pass/Fail**: ___________

#### Test 1.3: Permission Grant Flow
**Objective**: Verify app works after permission is granted

**Steps**:
1. Launch the app
2. Grant "All Files Access" permission
3. Proceed to folder selection

**Expected Results**:
- ✅ Permission is granted successfully
- ✅ Folder picker becomes accessible
- ✅ App can read file system

**Pass/Fail**: ___________

---

### Phase 2: Configuration Screen Testing

#### Test 2.1: Folder Selection
**Objective**: Verify folder picker works correctly

**Steps**:
1. Tap "Select Folder to Clean" button
2. Navigate to a test folder (e.g., Downloads, DCIM)
3. Select the folder

**Expected Results**:
- ✅ System document picker opens
- ✅ User can navigate folder hierarchy
- ✅ Selected path is displayed correctly
- ✅ Path format: `/storage/emulated/0/...`

**Pass/Fail**: ___________

#### Test 2.2: Date Range Selection
**Objective**: Verify date range picker functionality

**Steps**:
1. Tap "Start Date" field
2. Select a date (e.g., 30 days ago)
3. Tap "End Date" field
4. Select today's date

**Expected Results**:
- ✅ Date picker dialog opens
- ✅ Selected dates are displayed in readable format
- ✅ End date cannot be before start date
- ✅ "All Time" option is available

**Pass/Fail**: ___________

#### Test 2.3: Sort Options
**Objective**: Verify sort option selection

**Steps**:
1. Tap each sort option:
   - Largest to Smallest
   - Smallest to Largest
   - Newest to Oldest

**Expected Results**:
- ✅ Only one option can be selected at a time
- ✅ Selection is visually indicated
- ✅ Default selection is "Largest to Smallest"

**Pass/Fail**: ___________

#### Test 2.4: Start Analysis Button
**Objective**: Verify analysis can be initiated

**Steps**:
1. Select a folder with 100+ files
2. Set date range (optional)
3. Choose sort option
4. Tap "Start Analysis"

**Expected Results**:
- ✅ Button is enabled when folder is selected
- ✅ Navigation to Analysis Screen occurs
- ✅ No crash or freeze

**Pass/Fail**: ___________

---

### Phase 3: Analysis Screen Testing

#### Test 3.1: Progress Indicator
**Objective**: Verify progress is displayed during scanning

**Steps**:
1. Start analysis on a folder with 500+ files
2. Observe the progress indicator

**Expected Results**:
- ✅ Linear progress bar is visible
- ✅ Progress updates smoothly (0% to 100%)
- ✅ Status text shows "Analyzing files..."
- ✅ File count updates in real-time

**Pass/Fail**: ___________

#### Test 3.2: Background Service Notification
**Objective**: Verify foreground service notification appears

**Steps**:
1. Start analysis
2. Pull down notification shade
3. Observe the notification

**Expected Results**:
- ✅ Notification appears with app icon
- ✅ Notification shows "Scanning files..."
- ✅ Notification is persistent (cannot be swiped away)
- ✅ Tapping notification returns to app

**Pass/Fail**: ___________

#### Test 3.3: Background Persistence
**Objective**: Verify scanning continues when app is minimized

**Steps**:
1. Start analysis on a large folder (1000+ files)
2. Press Home button to minimize app
3. Wait 10 seconds
4. Return to app

**Expected Results**:
- ✅ Scanning continues in background
- ✅ Notification remains visible
- ✅ Progress is maintained when returning
- ✅ No data loss or restart

**Pass/Fail**: ___________

#### Test 3.4: Large Folder Performance
**Objective**: Verify app handles 10,000+ files without crashing

**Steps**:
1. Select a folder with 10,000+ files (e.g., WhatsApp Media)
2. Start analysis
3. Monitor memory usage (optional: use Android Studio Profiler)

**Expected Results**:
- ✅ No OutOfMemoryError crash
- ✅ Progress updates smoothly
- ✅ App remains responsive
- ✅ Scanning completes successfully

**Pass/Fail**: ___________

#### Test 3.5: Automatic Navigation
**Objective**: Verify automatic transition to Results Screen

**Steps**:
1. Start analysis on a small folder (50 files)
2. Wait for completion

**Expected Results**:
- ✅ Automatically navigates to Results Screen
- ✅ File list is populated
- ✅ No manual intervention required

**Pass/Fail**: ___________

---

### Phase 4: Results Screen Testing

#### Test 4.1: File List Display
**Objective**: Verify file list renders correctly

**Steps**:
1. Complete analysis
2. Scroll through the file list

**Expected Results**:
- ✅ All files are displayed
- ✅ File names are visible (truncated if long)
- ✅ File sizes are formatted (KB, MB, GB)
- ✅ Dates are in readable format
- ✅ File type icons are correct
- ✅ Smooth scrolling (no lag)

**Pass/Fail**: ___________

#### Test 4.2: File Type Icons
**Objective**: Verify correct icons for different file types

**Steps**:
1. Observe icons for:
   - Images (.jpg, .png)
   - Videos (.mp4, .avi)
   - Audio (.mp3, .wav)
   - Documents (.pdf, .docx)
   - Archives (.zip, .rar)
   - Unknown types

**Expected Results**:
- ✅ Each file type has appropriate icon
- ✅ Icons are visually distinct
- ✅ Unknown types show generic file icon

**Pass/Fail**: ___________

#### Test 4.3: Single File Selection
**Objective**: Verify checkbox selection works

**Steps**:
1. Tap checkbox on a single file
2. Observe selection state

**Expected Results**:
- ✅ Checkbox becomes checked
- ✅ Selection count updates (e.g., "Selected: 1 files")
- ✅ Total size updates
- ✅ Delete button becomes enabled

**Pass/Fail**: ___________

#### Test 4.4: Multi-Select
**Objective**: Verify multiple file selection

**Steps**:
1. Select 5 different files
2. Observe selection stats

**Expected Results**:
- ✅ All selected files show checked state
- ✅ Selection count shows "Selected: 5 files"
- ✅ Total size is sum of all selected files
- ✅ Can deselect individual files

**Pass/Fail**: ___________

#### Test 4.5: Select All / Deselect All
**Objective**: Verify bulk selection controls

**Steps**:
1. Tap "Select All" button (if available)
2. Tap "Deselect All" button

**Expected Results**:
- ✅ Select All: All checkboxes become checked
- ✅ Deselect All: All checkboxes become unchecked
- ✅ Stats update correctly

**Pass/Fail**: ___________

#### Test 4.6: File Info Dialog
**Objective**: Verify file information popup

**Steps**:
1. Tap the "i" (info) icon on any file
2. Review the displayed information

**Expected Results**:
- ✅ Dialog appears with file details
- ✅ Shows full file path
- ✅ Shows full file name
- ✅ Shows exact size in bytes
- ✅ Shows creation/modification date
- ✅ Dialog can be dismissed

**Pass/Fail**: ___________

#### Test 4.7: Open File Action
**Objective**: Verify file can be opened in external app

**Steps**:
1. Tap "Open" icon on an image file
2. Select an app to open it (e.g., Gallery)

**Expected Results**:
- ✅ Intent chooser appears
- ✅ File opens in selected app
- ✅ File content is correct
- ✅ Returns to Storage Cleaner when done

**Pass/Fail**: ___________

#### Test 4.8: Single File Delete
**Objective**: Verify individual file deletion

**Steps**:
1. Tap "Trash" icon on a test file
2. Confirm deletion in dialog

**Expected Results**:
- ✅ Confirmation dialog appears
- ✅ Dialog shows file name
- ✅ "Cancel" button dismisses dialog
- ✅ "Delete" button removes file
- ✅ File disappears from list
- ✅ File is deleted from storage
- ✅ Toast message confirms deletion

**Pass/Fail**: ___________

#### Test 4.9: Bulk Delete
**Objective**: Verify multiple file deletion

**Steps**:
1. Select 10 test files
2. Tap "Delete" button in top bar
3. Confirm deletion

**Expected Results**:
- ✅ Confirmation dialog shows count (e.g., "Delete 10 files?")
- ✅ Dialog shows total size
- ✅ "Cancel" preserves files
- ✅ "Delete" removes all selected files
- ✅ Files disappear from list
- ✅ Files are deleted from storage
- ✅ Toast message confirms deletion

**Pass/Fail**: ___________

#### Test 4.10: Delete Cancellation
**Objective**: Verify deletion can be cancelled

**Steps**:
1. Select files
2. Tap "Delete" button
3. Tap "Cancel" in confirmation dialog

**Expected Results**:
- ✅ Dialog is dismissed
- ✅ No files are deleted
- ✅ Selection is preserved
- ✅ App remains in same state

**Pass/Fail**: ___________

---

### Phase 5: Edge Cases & Error Handling

#### Test 5.1: Empty Folder
**Objective**: Verify handling of folders with no files

**Steps**:
1. Select an empty folder
2. Start analysis

**Expected Results**:
- ✅ Analysis completes quickly
- ✅ Results Screen shows "No files found" message
- ✅ No crash or error

**Pass/Fail**: ___________

#### Test 5.2: No Files Match Date Range
**Objective**: Verify handling when date filter excludes all files

**Steps**:
1. Select a folder
2. Set date range to future dates
3. Start analysis

**Expected Results**:
- ✅ Analysis completes
- ✅ Results Screen shows "No files match criteria"
- ✅ No crash

**Pass/Fail**: ___________

#### Test 5.3: Permission Revoked During Scan
**Objective**: Verify handling of permission revocation

**Steps**:
1. Start analysis
2. Go to Settings → Apps → Storage Cleaner
3. Revoke "All Files Access" permission
4. Return to app

**Expected Results**:
- ✅ Scanning stops gracefully
- ✅ Error message is displayed
- ✅ No crash
- ✅ User can re-grant permission

**Pass/Fail**: ___________

#### Test 5.4: File Deleted by Another App
**Objective**: Verify handling when file is deleted externally

**Steps**:
1. Complete analysis
2. Use file manager to delete a file shown in list
3. Try to open or delete that file in Storage Cleaner

**Expected Results**:
- ✅ Error message: "File not found"
- ✅ File is removed from list
- ✅ No crash

**Pass/Fail**: ___________

#### Test 5.5: Low Storage Space
**Objective**: Verify app behavior with low storage

**Steps**:
1. Fill device storage to <100 MB free
2. Use the app normally

**Expected Results**:
- ✅ App functions normally
- ✅ Deletion frees up space
- ✅ No crash due to low storage

**Pass/Fail**: ___________

#### Test 5.6: Screen Rotation
**Objective**: Verify state preservation during rotation

**Steps**:
1. Select files
2. Rotate device (portrait ↔ landscape)
3. Observe state

**Expected Results**:
- ✅ File list is preserved
- ✅ Selection state is preserved
- ✅ Stats remain accurate
- ✅ No crash or data loss

**Pass/Fail**: ___________

#### Test 5.7: App Killed by System
**Objective**: Verify recovery after process death

**Steps**:
1. Start analysis
2. Force stop app from Settings
3. Relaunch app

**Expected Results**:
- ✅ App restarts cleanly
- ✅ No corrupted state
- ✅ User can start new analysis

**Pass/Fail**: ___________

---

### Phase 6: Performance & Memory Testing

#### Test 6.1: Memory Usage (Small Dataset)
**Objective**: Measure memory usage with 100 files

**Steps**:
1. Use Android Studio Profiler or `adb shell dumpsys meminfo`
2. Analyze 100 files
3. Record memory usage

**Expected Results**:
- ✅ Memory usage < 100 MB
- ✅ No memory leaks
- ✅ Garbage collection is efficient

**Memory Usage**: ___________ MB

#### Test 6.2: Memory Usage (Large Dataset)
**Objective**: Measure memory usage with 10,000 files

**Steps**:
1. Analyze 10,000 files
2. Record memory usage

**Expected Results**:
- ✅ Memory usage < 300 MB
- ✅ No OutOfMemoryError
- ✅ App remains responsive

**Memory Usage**: ___________ MB

#### Test 6.3: Scrolling Performance
**Objective**: Verify smooth scrolling with large lists

**Steps**:
1. Load 5,000+ files
2. Scroll rapidly through list
3. Observe frame rate

**Expected Results**:
- ✅ Smooth scrolling (60 FPS)
- ✅ No jank or stuttering
- ✅ Images load progressively

**Pass/Fail**: ___________

#### Test 6.4: Battery Usage
**Objective**: Verify reasonable battery consumption

**Steps**:
1. Fully charge device
2. Run analysis on 5,000 files
3. Check battery usage in Settings

**Expected Results**:
- ✅ Battery drain < 5% for 5,000 files
- ✅ No excessive CPU usage
- ✅ Service stops after completion

**Battery Drain**: ___________ %

---

### Phase 7: UI/UX Testing

#### Test 7.1: Material 3 Design
**Objective**: Verify UI follows Material Design 3 guidelines

**Steps**:
1. Review all screens
2. Check colors, typography, spacing

**Expected Results**:
- ✅ Consistent color scheme
- ✅ Proper elevation and shadows
- ✅ Material 3 components used
- ✅ Accessible contrast ratios

**Pass/Fail**: ___________

#### Test 7.2: Dark Mode Support
**Objective**: Verify dark mode compatibility

**Steps**:
1. Enable dark mode in device settings
2. Launch app
3. Navigate through all screens

**Expected Results**:
- ✅ App respects system dark mode
- ✅ All text is readable
- ✅ No white backgrounds in dark mode
- ✅ Icons are visible

**Pass/Fail**: ___________

#### Test 7.3: Accessibility
**Objective**: Verify accessibility features

**Steps**:
1. Enable TalkBack
2. Navigate through app
3. Test with large font sizes

**Expected Results**:
- ✅ All buttons have content descriptions
- ✅ TalkBack announces elements correctly
- ✅ UI scales with font size
- ✅ Touch targets are ≥48dp

**Pass/Fail**: ___________

#### Test 7.4: Toast Messages
**Objective**: Verify user feedback messages

**Steps**:
1. Perform various actions (delete, open, etc.)
2. Observe toast messages

**Expected Results**:
- ✅ Success messages are clear
- ✅ Error messages are informative
- ✅ Toasts don't overlap
- ✅ Duration is appropriate

**Pass/Fail**: ___________

---

## Test Summary

### Overall Results

| Phase | Tests Passed | Tests Failed | Pass Rate |
|-------|--------------|--------------|-----------|
| Phase 1: Permissions | ___/3 | ___/3 | ___% |
| Phase 2: Configuration | ___/4 | ___/4 | ___% |
| Phase 3: Analysis | ___/5 | ___/5 | ___% |
| Phase 4: Results | ___/10 | ___/10 | ___% |
| Phase 5: Edge Cases | ___/7 | ___/7 | ___% |
| Phase 6: Performance | ___/4 | ___/4 | ___% |
| Phase 7: UI/UX | ___/4 | ___/4 | ___% |
| **TOTAL** | **___/37** | **___/37** | **___%** |

### Critical Issues Found
1. _______________________________________
2. _______________________________________
3. _______________________________________

### Minor Issues Found
1. _______________________________________
2. _______________________________________
3. _______________________________________

### Recommendations
1. _______________________________________
2. _______________________________________
3. _______________________________________

---

## Automated Testing (Optional)

For developers who want to add automated tests:

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### UI Tests (Compose)
```kotlin
@Test
fun testFileSelection() {
    composeTestRule.setContent {
        ResultsScreen(viewModel = testViewModel)
    }
    composeTestRule.onNodeWithTag("file_checkbox_0").performClick()
    composeTestRule.onNodeWithText("Selected: 1 files").assertExists()
}
```

---

## Troubleshooting

### Issue: APK Won't Install
**Solution**: Enable "Install from Unknown Sources" in Settings → Security

### Issue: Permission Dialog Doesn't Appear
**Solution**: Manually grant permission in Settings → Apps → Storage Cleaner → Permissions

### Issue: Folder Picker Crashes
**Solution**: Ensure device is running Android 11+ (API 30)

### Issue: Files Not Showing
**Solution**: Verify "All Files Access" permission is granted

### Issue: App Crashes on Large Folders
**Solution**: Report issue with device model and Android version

---

## Reporting Issues

If you encounter any bugs or issues during testing:

1. **GitHub Issues**: https://github.com/Rishin-zartek/anshif_test/issues
2. **Include**:
   - Device model and Android version
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots/logs if possible

---

## Test Environment Details

**Tester Name**: _______________________  
**Date**: _______________________  
**Device Model**: _______________________  
**Android Version**: _______________________  
**App Version**: 1.0 (Build 1)  
**APK Size**: 50 MB  

---

## Conclusion

This testing guide covers all critical functionality of the Android Storage Cleaner app. Completing all tests ensures the app is production-ready and handles edge cases gracefully.

**Minimum Pass Rate for Production**: 90% (33/37 tests)

---

**Document Version**: 1.0  
**Last Updated**: February 12, 2026  
**Maintained By**: Development Team
