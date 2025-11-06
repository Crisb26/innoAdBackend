@echo off
set PGPASSWORD=Cris930226**
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -p 5432 -c "DROP DATABASE IF EXISTS innoad_db;"
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -p 5432 -c "CREATE DATABASE innoad_db WITH OWNER = postgres ENCODING = 'UTF8';"
echo.
echo Base de datos innoad_db creada exitosamente!
echo.
pause
