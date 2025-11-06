# Archivo de configuracion para comandos rapidos de Git
# Guarda tus comandos mas utilizados

# Navegacion
alias proj="cd 'c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend'"
alias back="cd 'c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend'"

# Git rapido
alias gs="git status"
alias ga="git add -A"
alias gc="git commit -m"
alias gp="git push origin main"
alias gl="git log --oneline -10"
alias gd="git diff"

# Maven rapido
alias mrun="mvn spring-boot:run"
alias mcompile="mvn compile"
alias mpackage="mvn clean package -DskipTests"
alias mclean="mvn clean"

# Desarrollo rapido
alias start-backend="mvn spring-boot:run"
alias compile-backend="mvn clean package -DskipTests"
alias swagger="start http://localhost:8080/swagger-ui.html"
alias h2="start http://localhost:8080/h2-console"

# Comandos combinados
alias deploy="mvn clean package -DskipTests && java -jar target/innoad-backend-2.0.0.jar"
alias quick-commit="git add -A && git commit -m 'Quick update' && git push origin main"

# URLs utiles
alias open-swagger="start http://localhost:8080/swagger-ui.html"
alias open-h2="start http://localhost:8080/h2-console"
alias open-github="start https://github.com/Crisb26/innoAdBackend"

# Comandos de limpieza
alias clean-target="rmdir /s /q target"
alias clean-all="mvn clean && rmdir /s /q target"

echo "Aliases cargados! Usa 'help-innoad' para ver todos los comandos."