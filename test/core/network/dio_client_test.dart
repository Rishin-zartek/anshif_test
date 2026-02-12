import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:kerala_diet_tracker/core/network/dio_client.dart';

void main() {
  group('DioClient', () {
    late DioClient dioClient;

    setUp(() {
      dioClient = DioClient(baseUrl: 'https://test.api.com');
    });

    test('should initialize with default base URL when not provided', () {
      // Act
      final client = DioClient();

      // Assert
      expect(client.dio.options.baseUrl, 'https://api.keraladiet.com');
    });

    test('should initialize with custom base URL when provided', () {
      // Act
      final client = DioClient(baseUrl: 'https://custom.api.com');

      // Assert
      expect(client.dio.options.baseUrl, 'https://custom.api.com');
    });

    test('should have correct default headers', () {
      // Assert
      expect(dioClient.dio.options.headers['Content-Type'], 'application/json');
      expect(dioClient.dio.options.headers['Accept'], 'application/json');
    });

    test('should have correct timeout settings', () {
      // Assert
      expect(dioClient.dio.options.connectTimeout, const Duration(seconds: 30));
      expect(dioClient.dio.options.receiveTimeout, const Duration(seconds: 30));
    });

    test('should have LogInterceptor added', () {
      // Assert
      final hasLogInterceptor = dioClient.dio.interceptors
          .any((interceptor) => interceptor is LogInterceptor);
      expect(hasLogInterceptor, true);
    });

    test('should have InterceptorsWrapper added', () {
      // Assert
      final hasInterceptorsWrapper = dioClient.dio.interceptors
          .any((interceptor) => interceptor is InterceptorsWrapper);
      expect(hasInterceptorsWrapper, true);
    });

    test('should expose dio instance', () {
      // Assert
      expect(dioClient.dio, isA<Dio>());
    });

    test('get method should return Future<Response>', () {
      // Arrange
      final path = '/test';
      final queryParams = {'key': 'value'};

      // Act
      final result = dioClient.get(path, queryParameters: queryParams);

      // Assert
      expect(result, isA<Future<Response>>());
    });

    test('post method should return Future<Response>', () {
      // Arrange
      final path = '/test';
      final data = {'name': 'test'};
      final queryParams = {'key': 'value'};

      // Act
      final result = dioClient.post(path, data: data, queryParameters: queryParams);

      // Assert
      expect(result, isA<Future<Response>>());
    });

    test('put method should return Future<Response>', () {
      // Arrange
      final path = '/test';
      final data = {'name': 'test'};
      final queryParams = {'key': 'value'};

      // Act
      final result = dioClient.put(path, data: data, queryParameters: queryParams);

      // Assert
      expect(result, isA<Future<Response>>());
    });

    test('delete method should return Future<Response>', () {
      // Arrange
      final path = '/test';
      final data = {'id': '123'};
      final queryParams = {'key': 'value'};

      // Act
      final result = dioClient.delete(path, data: data, queryParameters: queryParams);

      // Assert
      expect(result, isA<Future<Response>>());
    });
  });
}
