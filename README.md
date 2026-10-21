# Ritmo78 🏃‍♂️💧
Planner pessoal para corridas (progressão de 2 km para 5 km), hidratação, refeições com fotos e pesagens.

## Instalar
- **Android APK:** https://github.com/luisroxxx/Ritimo78/releases/latest/download/Ritmo78.apk — link funciona **após** a compilação e publicação automática concluírem.
- **iPhone/Android como PWA:** https://luisroxxx.github.io/Ritimo78/ — abrir no Safari/Chrome e adicionar à tela inicial após GitHub Pages publicar.
- GitHub Actions em **Actions → Gerar APK Android** fornece relatório e artefato da compilação.

## Nota importante sobre iPhone
O iOS não permite instalar APK. A PWA não lê o Apple Saúde. Para importar corridas automaticamente, será preciso compilar o projeto Swift/HealthKit fornecido anteriormente com Xcode, assinar e instalar legalmente no aparelho. Este commit não fornece IPA iOS assinado.

## Privacidade
Fotos e dados de saúde são mantidos localmente no aparelho/navegador; não existe backend de sincronização. Exportações do diário não incluem as fotografias. Desinstalar ou limpar dados pode apagar registros.

## Build Android
O projeto em android/ usa Gradle 8.9, plugin Android 8.7.3 e Android SDK 35, com suporte mínimo ao Android 8.0. O fluxo de Actions gera APK debug: a chave de assinatura pode mudar entre builds e exigir desinstalar a versão anterior, apagando dados. Não é ainda uma distribuição final de produção.
