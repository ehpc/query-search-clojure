(ns query-search.parser-test
  "Тестирование парсера ответа поиска по блогам."
  (:require [clojure.test :refer [deftest testing is]]
            [query-search.parser :refer [parse]]))

(def sample "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<rss xmlns:yablogs=\"urn:yandex-blogs\" xmlns:wfw=\"http://wellformedweb.org/CommentAPI/\" version=\"2.0\">\n  <channel>\n    <link>https://yandex.ru/blogs/rss/search?numdoc=10&amp;text=livejournal</link>\n    <title>livejournal — Яндекс.Поиск по блогам</title>\n    <image>\n      <url>https://img.yandex.net/i/logo100x43.png</url>\n      <title>Поиск Яндекса по блогам</title>\n      <link>https://yandex.ru/blogs</link>\n      <width>100</width>\n      <height>43</height>\n    </image>\n    <ttl>60</ttl>\n    <generator>yandex.ru/blogs</generator>\n    <webMaster>support@blogs.yandex.ru</webMaster>\n    <copyright>noindex</copyright>\n    <description>Результаты поиска Яндекса по блогам и форумам по запросу: «livejournal»</description>\n    <yablogs:count>255899</yablogs:count>\n    <yablogs:more>https://yandex.ru/blogs/rss/search?p=1&amp;text=livejournal&amp;numdoc=10</yablogs:more>\n    <item>\n      <author>http://yana-igraeva.livejournal.com/</author>\n      <yablogs:author>yana_igraeva</yablogs:author>\n      <title>http://banguerski-alex.livejournal.com/289247.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:08:11 GMT</pubDate>\n      <guid>http://yana-igraeva.livejournal.com/1009665.html</guid>\n      <link>http://yana-igraeva.scala.com/1009665.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fyana-igraeva.livejournal.com%2F1009665.html</wfw:commentRss>\n      <yablogs:journal url=\"http://yana-igraeva.livejournal.com/\">yana_igraeva</yablogs:journal>\n      <description>http://banguerski-alex.&lt;b&gt;livejournal&lt;/b&gt;.com/289247.html.</description>\n    </item>\n    <item>\n      <author>http://arpadhaizy.livejournal.com/</author>\n      <yablogs:author>arpadhaizy</yablogs:author>\n      <title>http://karhu53.livejournal.com/23920439.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:07:48 GMT</pubDate>\n      <guid>http://arpadhaizy.livejournal.com/6519850.html</guid>\n      <link>http://arpadhaizy.scala.com/6519850.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Farpadhaizy.livejournal.com%2F6519850.html</wfw:commentRss>\n      <yablogs:journal url=\"http://arpadhaizy.livejournal.com/\">arpadhaizy</yablogs:journal>\n      <description>http://karhu53.&lt;b&gt;livejournal&lt;/b&gt;.com/23920439.html.</description>\n    </item>\n    <item>\n      <author>http://pirandger.livejournal.com/</author>\n      <yablogs:author>pirandger</yablogs:author>\n      <title>http://prosevbout.livejournal.com/8263.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:07:27 GMT</pubDate>\n      <guid>http://pirandger.livejournal.com/7660.html</guid>\n      <link>http://pirandger.scala.com/7660.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fpirandger.livejournal.com%2F7660.html</wfw:commentRss>\n      <yablogs:journal url=\"http://pirandger.livejournal.com/\">pirandger</yablogs:journal>\n      <description>http://prosevbout.&lt;b&gt;livejournal&lt;/b&gt;.com/8263.html.</description>\n    </item>\n    <item>\n      <author>http://tranuctool.livejournal.com/</author>\n      <yablogs:author>tranuctool</yablogs:author>\n      <title>http://prosevbout.livejournal.com/8263.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:07:27 GMT</pubDate>\n      <guid>http://tranuctool.livejournal.com/8573.html</guid>\n      <link>http://tranuctool.scala.com/8573.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Ftranuctool.livejournal.com%2F8573.html</wfw:commentRss>\n      <yablogs:journal url=\"http://tranuctool.livejournal.com/\">tranuctool</yablogs:journal>\n      <description>http://prosevbout.&lt;b&gt;livejournal&lt;/b&gt;.com/8263.html.</description>\n    </item>\n    <item>\n      <author>http://ciomembre.livejournal.com/</author>\n      <yablogs:author>ciomembre</yablogs:author>\n      <title>http://topbcuson.livejournal.com/6981.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:07:26 GMT</pubDate>\n      <guid>http://ciomembre.livejournal.com/6632.html</guid>\n      <link>http://ciomembre.livejournal.com/6632.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fciomembre.livejournal.com%2F6632.html</wfw:commentRss>\n      <yablogs:journal url=\"http://ciomembre.livejournal.com/\">ciomembre</yablogs:journal>\n      <description>http://topbcuson.&lt;b&gt;livejournal&lt;/b&gt;.com/6981.html.</description>\n    </item>\n    <item>\n      <author>http://trance-se.livejournal.com/</author>\n      <yablogs:author>trance_se</yablogs:author>\n      <title>http://gur-ar.livejournal.com/276255.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:07:08 GMT</pubDate>\n      <guid>http://trance-se.livejournal.com/731962.html</guid>\n      <link>http://trance-se.livejournal.com/731962.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Ftrance-se.livejournal.com%2F731962.html</wfw:commentRss>\n      <yablogs:journal url=\"http://trance-se.livejournal.com/\">trance_se</yablogs:journal>\n      <description>http://gur-ar.&lt;b&gt;livejournal&lt;/b&gt;.com/276255.html.</description>\n    </item>\n    <item>\n      <author>http://ljmosobl.livejournal.com/</author>\n      <yablogs:author>ljmosobl</yablogs:author>\n      <title>http://mosobl.livejournal.com/373888.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:06:55 GMT</pubDate>\n      <guid>http://ljmosobl.livejournal.com/358855.html</guid>\n      <link>http://ljmosobl.livejournal.com/358855.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fljmosobl.livejournal.com%2F358855.html</wfw:commentRss>\n      <yablogs:journal url=\"http://ljmosobl.livejournal.com/\">ljmosobl</yablogs:journal>\n      <description>http://mosobl.&lt;b&gt;livejournal&lt;/b&gt;.com/373888.html.</description>\n    </item>\n    <item>\n      <author>http://putin-dictator.livejournal.com/</author>\n      <yablogs:author>putin_dictator</yablogs:author>\n      <title>Пора Путину бояться /// ликвидация элиты.</title>\n      <pubDate>Fri, 31 Mar 2017 15:06:50 GMT</pubDate>\n      <guid>http://putin-dictator.livejournal.com/856.html</guid>\n      <link>http://putin-dictator.vk.com/856.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fputin-dictator.livejournal.com%2F856.html</wfw:commentRss>\n      <yablogs:journal url=\"http://putin-dictator.livejournal.com/\">putin_dictator</yablogs:journal>\n      <description>http://dictator-putin.&lt;b&gt;livejournal&lt;/b&gt;.com.</description>\n    </item>\n    <item>\n      <author>http://quetrinat.livejournal.com/</author>\n      <yablogs:author>quetrinat</yablogs:author>\n      <title>http://satorbull.livejournal.com/6511.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:06:28 GMT</pubDate>\n      <guid>http://quetrinat.livejournal.com/6609.html</guid>\n      <link>http://quetrinat.vk.com/6609.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fquetrinat.livejournal.com%2F6609.html</wfw:commentRss>\n      <yablogs:journal url=\"http://quetrinat.livejournal.com/\">quetrinat</yablogs:journal>\n      <description>http://satorbull.&lt;b&gt;livejournal&lt;/b&gt;.com/6511.html.</description>\n    </item>\n    <item>\n      <author>http://darrimoun.livejournal.com/</author>\n      <yablogs:author>darrimoun</yablogs:author>\n      <title>http://acchiegsol.livejournal.com/8084.html</title>\n      <pubDate>Fri, 31 Mar 2017 15:06:27 GMT</pubDate>\n      <guid>http://darrimoun.livejournal.com/7323.html</guid>\n      <link>http://darrimoun.example.com/7323.html</link>\n      <wfw:commentRss>https://yandex.ru/blogs/rss/search?post=http%3A%2F%2Fdarrimoun.livejournal.com%2F7323.html</wfw:commentRss>\n      <yablogs:journal url=\"http://darrimoun.livejournal.com/\">darrimoun</yablogs:journal>\n      <description>http://acchiegsol.&lt;b&gt;livejournal&lt;/b&gt;.com/8084.html.</description>\n    </item>\n  </channel>\n</rss>")

(deftest parse-test
  (testing "Извлечение данных о доменах из ответа поиска по блогам."
    (is
      (some
        #(= (first %) "livejournal.com")
        (parse sample))
      "В ответе есть нужный домен.")))
