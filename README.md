# BE проекта OS Test

## Docker
- Для настройки локального окружения нужно запустить docker compose

## Swagger
- API локально можно гонять через Swagger
  - http://localhost:8080/swagger-ui/index.html

## Миграции
- Генерацию миграций можно проводить через плагин liquibase: diffChangelog. 
С помощью плагина происходит генерация недостающих changeset'ов через сравнение схем данных:
  - ожидаемая (задается через @Entity и их отношения) и текущая (то, что есть в БД)

## Правила работы c репозиторием
- Ветка именуется следующим образом:
  - feature/{номер таски в redmine}/краткое-описание-сделанного
- Коммиты именуются следующим образом:
  - refs #{номер таски в redmine} Описание того, что было сделано в рамках коммита
- Для слития в master нужно сформировать pull request и добавить Мишу, Никиту, Рому. 
- Давайте придерживаться линейной истории master'a

## Тестовые пользователи

- ADMIN - username: **batman_forever** password: **batman_forever**
- STUDENT - username: **dora_explorer** password: **dora_explorer**
- TEACHER - username: **catch_me_if_u_can** password: **catch_me_if_u_can**
