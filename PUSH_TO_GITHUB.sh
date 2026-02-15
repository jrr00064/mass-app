#!/bin/bash
# Script para subir MASS app a GitHub
# Uso: ./PUSH_TO_GITHUB.sh <github_username> <repo_name>

if [ $# -lt 2 ]; then
    echo "Uso: ./PUSH_TO_GITHUB.sh <github_username> <repo_name>"
    echo "Ejemplo: ./PUSH_TO_GITHUB.sh jrr00064 mass-app"
    exit 1
fi

USERNAME=$1
REPO=$2

echo "Creando repositorio en GitHub..."
echo "Necesitas tener gh CLI instalado o hacerlo manualmente en https://github.com/new"
echo ""
echo "Para crear con gh CLI:"
echo "  gh repo create $REPO --public --description \"MASS - Mindful Application Screen-time System for Nothing Phone\""
echo ""
echo "Para hacerlo manualmente:"
echo "  1. Ve a https://github.com/new"
echo "  2. Nombre del repo: $REPO"
echo "  3. Descripción: MASS - Mindful Application Screen-time System for Nothing Phone"
echo "  4. Crear repositorio"
echo ""
echo "Después de crear el repo, ejecuta:"
echo "  git remote add origin https://github.com/$USERNAME/$REPO.git"
echo "  git push -u origin master"
echo ""
echo "O ejecuta este script con --push después de crear el repo:"
echo "  ./PUSH_TO_GITHUB.sh $USERNAME $REPO --push"

if [ "$3" == "--push" ]; then
    echo ""
    echo "Configurando remote y subiendo..."
    git remote add origin "https://github.com/$USERNAME/$REPO.git"
    git push -u origin master
    echo ""
    echo "✅ Subido a https://github.com/$USERNAME/$REPO"
fi
