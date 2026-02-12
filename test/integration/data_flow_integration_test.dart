import 'package:flutter_test/flutter_test.dart';
import 'package:hive/hive.dart';
import 'package:kerala_diet_tracker/core/database/hive_config.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('Data Flow Integration Tests', () {
    setUp(() async {
      Hive.init('test_integration_hive');
    });

    tearDown(() async {
      await Hive.deleteFromDisk();
    });

    test('should store and retrieve food data through HiveConfig', () async {
      // Arrange
      final foodData = {
        'id': 'rice',
        'name': 'Rice',
        'calories': 130,
        'protein': 2.7,
        'carbs': 28,
        'fat': 0.3
      };

      // Act
      final foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      await foodBox.put('rice', foodData);

      // Assert
      expect(foodBox.get('rice'), isNotNull);
      final retrievedData = foodBox.get('rice') as Map;
      expect(retrievedData['name'], 'Rice');
      expect(retrievedData['calories'], 130);
      expect(retrievedData['protein'], 2.7);
    });

    test('should store and retrieve recipe data through HiveConfig', () async {
      // Arrange
      final recipeData = {
        'id': 'sambar',
        'name': 'Sambar',
        'ingredients': ['dal', 'vegetables', 'tamarind']
      };

      // Act
      final recipeBox = await HiveConfig.getBox(HiveConfig.recipeBoxName);
      await recipeBox.put('sambar', recipeData);

      // Assert
      expect(recipeBox.get('sambar'), isNotNull);
      final retrievedData = recipeBox.get('sambar') as Map;
      expect(retrievedData['name'], 'Sambar');
      expect(retrievedData['ingredients'], isA<List>());
      expect((retrievedData['ingredients'] as List).length, 3);
    });

    test('should handle multiple box operations', () async {
      // Act
      final foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      await foodBox.put('food1', {'id': 'food1', 'name': 'Food 1'});
      await foodBox.put('food2', {'id': 'food2', 'name': 'Food 2'});
      await foodBox.put('food3', {'id': 'food3', 'name': 'Food 3'});

      // Assert
      expect(foodBox.length, 3);
      expect(foodBox.get('food1'), isNotNull);
      expect(foodBox.get('food2'), isNotNull);
      expect(foodBox.get('food3'), isNotNull);
    });

    test('should maintain data integrity across box reopening', () async {
      // Arrange & Act
      var foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      await foodBox.put('persistent_food', {
        'id': 'persistent',
        'name': 'Persistent Food',
        'calories': 150
      });
      await foodBox.close();

      // Reopen the box
      foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);

      // Assert
      expect(foodBox.get('persistent_food'), isNotNull);
      final data = foodBox.get('persistent_food') as Map;
      expect(data['name'], 'Persistent Food');
      expect(data['calories'], 150);
    });

    test('should handle clearAllData operation', () async {
      // Arrange
      final foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      final recipeBox = await HiveConfig.getBox(HiveConfig.recipeBoxName);
      await foodBox.put('food1', {'id': 'food1', 'name': 'Food 1'});
      await recipeBox.put('recipe1', {'id': 'recipe1', 'name': 'Recipe 1'});

      // Act
      await HiveConfig.clearAllData();

      // Assert - Boxes should be deleted
      expect(Hive.isBoxOpen(HiveConfig.foodBoxName), false);
      expect(Hive.isBoxOpen(HiveConfig.recipeBoxName), false);
    });

    test('should allow data operations after clearAllData', () async {
      // Arrange
      var foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      await foodBox.put('old_food', {'id': 'old', 'name': 'Old Food'});
      await HiveConfig.clearAllData();

      // Act - Reopen and add new data
      foodBox = await HiveConfig.getBox(HiveConfig.foodBoxName);
      await foodBox.put('new_food', {'id': 'new', 'name': 'New Food'});

      // Assert
      expect(foodBox.get('old_food'), isNull);
      expect(foodBox.get('new_food'), isNotNull);
    });

    test('should handle concurrent box access', () async {
      // Act
      final foodBox1 = await HiveConfig.getBox(HiveConfig.foodBoxName);
      final foodBox2 = await HiveConfig.getBox(HiveConfig.foodBoxName);

      // Assert - Should return the same box instance
      expect(identical(foodBox1, foodBox2), true);
    });
  });
}
