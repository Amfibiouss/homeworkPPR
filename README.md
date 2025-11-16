Программа написана на языке программирования Java с использованием Spring и Hibernate на серверной стороне, а на клиентской с использованием  Vue, Bootstrap и Thymeleaf. Компилируется и запускается как и обычное springboot приложение. Прослушивает порт 8080.



Исходный код разделен на несколько каталогов:

homeworkPPR2/src/main/java/com/example/laba содержит главный класс приложения

homeworkPPR2/src/main/java/com/example/authentication содержит кастомные классы производные от классов из Spring Security обеспечивающие аутентификацию пользователей

homeworkPPR2/src/main/java/com/example/configs содержит классы конфигураций с настройками компонентов

homeworkPPR2/src/main/java/com/example/controllers содержит классы контроллеров

homeworkPPR2/src/main/java/com/example/entities содержит классы сущностейс с помощью ORM представляющих таблицы из базы данных 

homeworkPPR2/src/main/java/com/example/filters содержит фильтры обрабатывающие запросы к серверу перед передачей в контролеры

homeworkPPR2/src/main/java/com/example/interceptors содержат классы для перехвата сообщений отправленных через вебсокеты по протоколу STOMP

homeworkPPR2/src/main/java/com/example/json\_object содержит классы для передачи json сообщений от сервера к клиенту

homeworkPPR2/src/main/java/com/example/listeners содержит классы реагирующие на изменение состояния websocket сессии

homeworkPPR2/src/main/java/com/example/objects\_to\_fill\_templates содержит классы для передачи данных для заполнения шаблонов Thymeleaf

homeworkPPR2/src/main/java/com/example/services содержат классы реализующие различные сервисы. Сервисы содержащие в имени DAO отвечают за работу с базой данных.



homeworkPPR2/src/main/resources/static содержит статические ресурсы: css, иконки, изображения, js файлы

homeworkPPR2/src/main/resources/templates содержит шаблоны Thymeleaf 





Операции с базой данных реализуются через ORM Hibernate и могут работать с любой СУБД, которая соответствует стандарту SQL. В базе данных есть несколько таблиц, которые отображаются в ORM c помощью соответствующих классов. Они все начинаются на F чтобы избежать коллизий с зарезервированными в БД словами. 



FRoom - таблица для хранения данных о игровых комнатах. Игровая комната состоит из игровых персонажей, каналов (просто теги для сообщений, которые влияют на видимость сообщений для разных групп игроков), голосований и фаз (промежутков времени на которые разбита игра. Например, день - ночь). 

FCharacter, FChannel, FPoll, FStage - таблицы для персонажей, каналов, голосований и фаз соответственно

FMessage, FUser, FPunishment - имеют очевидное предназначение

Также есть таблица FStageFChannel которая нужна чтобы имитировать связь с атрибутами между FStage и FChannel









