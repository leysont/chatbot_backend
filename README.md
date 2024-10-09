# adira_core

## Structure (Docker containers)
| Name             | Port | Image              | Techs         | Volume               |
|------------------|------|--------------------|---------------|----------------------|
| dialog           | 8080 | adira_core.dialog  | Ktor, OpenAI  |                      |
| storage          | 8081 | adira_core.storage | Ktor, Exposed |                      |
| storage.database | 3306 | mariadb            | MariaDB       | adira_storage_volume |

## Build & launch
- For both `storage` and `dialog` (if `gradle` command is not available, use `.\gradlew.bat` [if on Windows] or `./gradlew` [if on Linux/macOS]):
  - `gradle storage:publishImageToLocalRegistry`
  - `gradle dialog:publishImageToLocalRegistry`
- To build & launch:
  - [for whole core] `docker-compose -f adira.yml`
  - [for only `storage`] `docker-compose -f dialog/adira.storage.yml`
  - [for only `dialog`] `docker-compose -f dialog/adira.dialog.yml`
