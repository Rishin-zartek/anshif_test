import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:hive/hive.dart';
import '../database/hive_config.dart';

class DataSeeder {
  static const String _seededKey = 'data_seeded';

  static Future<void> seedData() async {
    final box = await HiveConfig.getBox(HiveConfig.foodBoxName);
    
    final isSeeded = box.get(_seededKey, defaultValue: false);
    if (isSeeded == true) {
      print('Data already seeded');
      return;
    }

    try {
      final jsonString = await rootBundle.loadString('assets/data/kerala_foods.json');
      final jsonData = json.decode(jsonString) as Map<String, dynamic>;
      
      final foods = jsonData['foods'] as List;
      for (var food in foods) {
        await box.put(food['id'], food);
      }
      
      final recipeBox = await HiveConfig.getBox(HiveConfig.recipeBoxName);
      final recipes = jsonData['recipes'] as List;
      for (var recipe in recipes) {
        await recipeBox.put(recipe['id'], recipe);
      }
      
      await box.put(_seededKey, true);
      print('Data seeded successfully: ${foods.length} foods, ${recipes.length} recipes');
    } catch (e) {
      print('Error seeding data: $e');
    }
  }
}
