# About project :

This project is the back end part of my graduate project for bachelor's degree. 
It is a REST API for the humanitarian aid website.

### [Client app (Front End)](https://github.com/guzev-dev/bachelor-frontend)

Tech stack:

* Java 17
* Spring (Boot, Security, Data JPA)
* RDBMS: MySQL
* Google SMTP Server (Mailing)
* Caffeine (Caching) 

*!!! Project include big file - **data.sql** that contains SQL queries for initialization database (including photos and other docs) !!!*

---

# Про проєкт:

Цей проєкт є серверною частиною моєї дипломної роботи для отримання ступеня бакалавра. 
Він являє собою REST API для вебсайту гуманітарної допомоги.

### [Клієнтська частина кваліфікаційної роботи](https://github.com/guzev-dev/bachelor-frontend)

Tech stack:

* Java 17
* Spring (Boot, Security, Data JPA)
* RDBMS: MySQL
* Google SMTP Server (Розсилка електронної пошти)
* Caffeine (Кешування)

*!!! Проєкт включає великий файл - **data.sql**, що містить запити для початкової ініціалізації
бази даних (в тому числі й фото та інші документи).!!!*

---
### Deployment:

for correct work of smtp server you should define environmental variables:
* ***SPRING_MAIL_HOST*** - host of smtp server (*smtp.gmail.com* for Gmail);
* ***SPRING_MAIL_PORT*** - host port (Gmail: 465 for SSL, 587 for TLS/STARTTLS);
* ***SPRING_MAIL_USERNAME*** - email address in format *example@gmail.com*;
* ***SPRING_MAIL_PASSWORD*** - email account password (use application password for Gmail).