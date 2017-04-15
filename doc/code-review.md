# Code Review

## 2017-04-09

* Тестирование concurrency через фейковый веб-сервер (может уже есть такой).
* Конфигурации, если не dependency injection и управление состоянием, 
  то хотя бы передавать конфиг параметром, иначе его не mock'нуть.
* Использовать канал http-kit, избавиться от future, atom, with-channel? Только core.async.
* Исправить форматирование, threading macros.
* Заменить apply concat map на mapcat, посмотреть другие стандартные паттерны.
* Изучить монады.
* Overload с фейковым аргументом убрать.
* Тесты тяжелые, например, parser вызывает search, вместо моментальной подстановки xml.
* Разделить конфиги для prod/dev.
* Keyword в get-settings вместо строки?
* Схема concurrency.
* BDD-spec для concurrency.
