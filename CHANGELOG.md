# Change Log

## [Unreleased]
## Added
- Сервер для тестироваия ограничений по одновременным соединениям: testing/limiting-server
- Сервер для тестирования API блогов: testing/fake-server
## Changed
- Concurrency теперь тестируется через limiting-server, а не crawler.
- Конфигурация разделена на default и dev.
- blog-search/search использует монаду reader для доступа к настройкам приложения.
- parser_test больше не зависит от blog-search.
- Тесты теперь не зависят от среды выполнения и от внешних сервисов.
- Модуль stat теперь не зависит от blog-search.
### Removed
- Удалён модуль blog-search, так как его функциональность можно вынести в rest.

## 0.1.0 - 2017-04-07
### Added
- Вся основная функциональность.
- Тесты.
- Документация.

[Unreleased]: https://github.com/ehpc/query-search-clojure/compare/v0.1.0...HEAD
