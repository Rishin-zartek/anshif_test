import 'package:hive_flutter/hive_flutter.dart';
import 'package:path_provider/path_provider.dart';

class HiveConfig {
  HiveConfig._();

  static const int currentVersion = 1;
  static const String versionBoxName = 'app_version';
  
  static const String userBoxName = 'users';
  static const String foodBoxName = 'foods';
  static const String mealBoxName = 'meals';
  static const String recipeBoxName = 'recipes';
  static const String exerciseBoxName = 'exercises';
  static const String waterBoxName = 'water';
  static const String weightLogBoxName = 'weight_logs';

  static Future<void> initialize() async {
    final appDocDir = await getApplicationDocumentsDirectory();
    await Hive.initFlutter(appDocDir.path);
    
    await _checkAndMigrate();
  }

  static Future<void> _checkAndMigrate() async {
    final versionBox = await Hive.openBox<int>(versionBoxName);
    final storedVersion = versionBox.get('version', defaultValue: 0);
    
    if (storedVersion! < currentVersion) {
      await _runMigrations(storedVersion, currentVersion);
      await versionBox.put('version', currentVersion);
    }
  }

  static Future<void> _runMigrations(int from, int to) async {
    for (int version = from + 1; version <= to; version++) {
      await _migrate(version);
    }
  }

  static Future<void> _migrate(int toVersion) async {
    switch (toVersion) {
      case 1:
        await _migrateToV1();
        break;
      default:
        break;
    }
  }

  static Future<void> _migrateToV1() async {
    await Hive.openBox(userBoxName);
    await Hive.openBox(foodBoxName);
    await Hive.openBox(mealBoxName);
    await Hive.openBox(recipeBoxName);
    await Hive.openBox(exerciseBoxName);
    await Hive.openBox(waterBoxName);
    await Hive.openBox(weightLogBoxName);
  }

  static Future<Box<T>> getBox<T>(String boxName) async {
    if (!Hive.isBoxOpen(boxName)) {
      return await Hive.openBox<T>(boxName);
    }
    return Hive.box<T>(boxName);
  }

  static Future<void> clearAllData() async {
    await Hive.deleteBoxFromDisk(userBoxName);
    await Hive.deleteBoxFromDisk(foodBoxName);
    await Hive.deleteBoxFromDisk(mealBoxName);
    await Hive.deleteBoxFromDisk(recipeBoxName);
    await Hive.deleteBoxFromDisk(exerciseBoxName);
    await Hive.deleteBoxFromDisk(waterBoxName);
    await Hive.deleteBoxFromDisk(weightLogBoxName);
  }
}
