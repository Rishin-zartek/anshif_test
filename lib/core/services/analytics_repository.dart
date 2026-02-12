abstract class AnalyticsRepository {
  Future<void> logEvent(String eventName, {Map<String, dynamic>? parameters});
  Future<void> setUserId(String userId);
  Future<void> setUserProperty(String name, String value);
  Future<void> logScreenView(String screenName);
}

class OfflineAnalyticsRepository implements AnalyticsRepository {
  @override
  Future<void> logEvent(String eventName, {Map<String, dynamic>? parameters}) async {
    print('Analytics Event: $eventName, Parameters: $parameters');
  }

  @override
  Future<void> setUserId(String userId) async {
    print('Analytics User ID: $userId');
  }

  @override
  Future<void> setUserProperty(String name, String value) async {
    print('Analytics User Property: $name = $value');
  }

  @override
  Future<void> logScreenView(String screenName) async {
    print('Analytics Screen View: $screenName');
  }
}
