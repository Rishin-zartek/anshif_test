import 'package:flutter_test/flutter_test.dart';
import 'package:hive/hive.dart';
import 'package:kerala_diet_tracker/core/database/hive_config.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('HiveConfig', () {
    setUp(() async {
      Hive.init('test_hive');
    });

    tearDown(() async {
      await Hive.deleteFromDisk();
    });

    test('should have correct box names defined', () {
      expect(HiveConfig.userBoxName, 'users');
      expect(HiveConfig.foodBoxName, 'foods');
      expect(HiveConfig.mealBoxName, 'meals');
      expect(HiveConfig.recipeBoxName, 'recipes');
      expect(HiveConfig.exerciseBoxName, 'exercises');
      expect(HiveConfig.waterBoxName, 'water');
      expect(HiveConfig.weightLogBoxName, 'weight_logs');
      expect(HiveConfig.versionBoxName, 'app_version');
    });

    test('should have correct current version', () {
      expect(HiveConfig.currentVersion, 1);
    });

    test('getBox should open box if not already open', () async {
      // Act
      final box = await HiveConfig.getBox(HiveConfig.foodBoxName);

      // Assert
      expect(box, isNotNull);
      expect(box.isOpen, true);
      expect(box.name, HiveConfig.foodBoxName);
    });

    test('getBox should return existing box if already open', () async {
      // Arrange
      final firstBox = await HiveConfig.getBox(HiveConfig.foodBoxName);

      // Act
      final secondBox = await HiveConfig.getBox(HiveConfig.foodBoxName);

      // Assert
      expect(identical(firstBox, secondBox), true);
    });

    test('getBox should work with typed boxes', () async {
      // Act
      final box = await HiveConfig.getBox<String>(HiveConfig.userBoxName);

      // Assert
      expect(box, isNotNull);
      expect(box.isOpen, true);
    });

    test('clearAllData should delete all boxes from disk', () async {
      // Arrange - Create and populate boxes
      final foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      final mealBox = await HiveConfig.getBox(HiveConfig.mealBoxName);
      await foodBox.put('test', 'data');
      await mealBox.put('test', 'data');

      // Act
      await HiveConfig.clearAllData();

      // Assert - Boxes should be deleted
      expect(Hive.isBoxOpen(HiveConfig.foodBoxName), false);
      expect(Hive.isBoxOpen(HiveConfig.mealBoxName), false);
    });

    test('clearAllData should handle non-existent boxes gracefully', () async {
      // Act & Assert - Should not throw
      expect(() async => await HiveConfig.clearAllData(), returnsNormally);
    });
  });
}
