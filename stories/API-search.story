API /search


Как пользователь API
Я хочу получить статистику доменов блогов, в которых упоминаются заданные мной ключевые слова


Сценарий: Статистика по одному ключевому слову при первом запуске
Если система запущена впервые
И текущий запрос является первым по счёту
Когда пользователь обращается по URI /search?query=scala
То в ответ приходит JSON:
{
    "vk.com": 8,
    "sql.ru": 1,
    "bookpedia.ru": 1
}


Сценарий: Статистика по одному ключевому слову при повторном запросе
Если ранее выполнялся запрос по URI /search?query=scala
И текущий запрос является вторым по счёту
Когда пользователь обращается по URI /search?query=scala
То в ответ приходит JSON:
{
    "vk.com": 8,
    "sql.ru": 1,
    "bookpedia.ru": 1
}


Сценарий: Статистика по другому ключевому слову
Если ранее выполнялись только запросы по URI /search?query=scala
И текущий запрос является третьим по счёту
Когда пользователь обращается по URI /search?query=bdd
То в ответ приходит JSON:
{
    "vk.com": 10
}


Сценарий: Статистика по двум запросам сразу
Когда пользователь обращается по URI /search?query=puppy&query=livejournal
То в ответ приходит JSON:
{
    "vk.com": 8,
    "twitter.com": 2,
    "livejournal.com": 8,
    "kharkovforum.com": 1
}


Сценарий: Запрос с русскими буквами
Когда пользователь обращается по URI /search?query=запрос
То в ответ приходит JSON:
{
    "vk.com": 2,
    "uincar.ru": 1,
    "mediaryazan.ru": 1,
    "pravo.ru": 1,
    "zhmak.info": 1,
    "livejournal.com": 1,
    "sdelanounas.ru": 1,
    "ecigtalk.ru": 1,
    "twitter.com": 1
}

Сценарий: Запрос с повторяющимися ссылками в ответе API Yandex
Если система запущена впервые
И текущий запрос является первым по счёту
И внутренний запрос к API Yandex "магическим" образом получает 10 одинаковых ссылок с доменом "vk.com" в ответе
Когда пользователь обращается по URI /search?query=повтор
То в ответ приходит JSON:
{
    "vk.com": 1
}
