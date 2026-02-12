import 'package:flutter_test/flutter_test.dart';
import 'package:hive/hive.dart';
import 'package:kerala_diet_tracker/core/database/hive_config.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('DataSeeder', () {
    late Box foodBox;
    late Box recipeBox;

    setUp(() async {
      // Initialize Hive with in-memory storage for testing
      Hive.init('test_hive');
      foodBox = await Hive.openBox(HiveConfig.foodBoxName);
      recipeBox = await Hive.openBox(HiveConfig.recipeBoxName);
    });

    tearDown(() async {
      await foodBox.clear();
      await recipeBox.clear();
      await foodBox.close();
      await recipeBox.close();
      await Hive.deleteFromDisk();
    });

    test('should check if data is already seeded', () async {
      // Arrange
      await foodBox.put('data_seeded', true);

      // Act
      final isSeeded = foodBox.get('data_seeded', defaultValue: false);

      // Assert
      expect(isSeeded, true);
    });

    test('should return false when data is not seeded', () async {
      // Act
      final isSeeded = foodBox.get('data_seeded', defaultValue: false);

      // Assert
      expect(isSeeded, false);
    });

    test('should be able to store food data in Hive', () async {
      // Arrange
      final foodData = {'id': 'rice', 'name': 'Rice', 'calories': 130};

      // Act
      await foodBox.put('rice', foodData);

      // Assert
      expect(foodBox.get('rice'), isNotNull);
      expect((foodBox.get('rice') as Map)['name'], 'Rice');
      expect((foodBox.get('rice') as Map)['calories'], 130);
    });

    test('should be able to store recipe data in Hive', () async {
      // Arrange
      final recipeData = {
        'id': 'sambar',
        'name': 'Sambar',
        'ingredients': ['dal', 'vegetables']
      };

      // Act
      await recipeBox.put('sambar', recipeData);

      // Assert
      expect(recipeBox.get('sambar'), isNotNull);
      expect((recipeBox.get('sambar') as Map)['name'], 'Sambar');
      expect((recipeBox.get('sambar') as Map)['ingredients'], isA<List>());
    });

    test('should be able to store multiple foods', () async {
      // Arrange & Act
      await foodBox.put('rice', {'id': 'rice', 'name': 'Rice'});
      await foodBox.put('dosa', {'id': 'dosa', 'name': 'Dosa'});
      await foodBox.put('idli', {'id': 'idli', 'name': 'Idli'});

      // Assert
      expect(foodBox.length, 3);
      expect(foodBox.get('rice'), isNotNull);
      expect(foodBox.get('dosa'), isNotNull);
      expect(foodBox.get('idli'), isNotNull);
    });

    test('should persist seeded flag', () async {
      // Act
      await foodBox.put('data_seeded', true);

      // Assert
      expect(foodBox.get('data_seeded'), true);
    });
  });
}
