# Ritmo78 v0.5 — IA de refeições e notificações no iOS

## Recursos disponíveis imediatamente
- Perfil individual com nome, idade, peso e meta calórica configurada manualmente.
- Estimativa de hidratação de referência: 30–35 ml/kg/dia (não é prescrição; clínica, exercício e clima mudam as necessidades).
- Registrar volume personalizado (inclusive 1.000 ml), ver quanto falta e lembretes quando o app está aberto.
- Diário alimentar com foto guardada no IndexedDB local, soma de kcal e proteína registrada.
- Calculadora de alimentos por porções informadas; valores médios aproximados por 100g.
- Saldo calórico diário e resumo mensal de comida, água e corrida.

## Foto analisada por IA — infraestrutura pendente
O botão "Analisar foto com IA" está preparado, mas **não existe serviço de visão computacional implantado**.
Configure um servidor HTTPS com rota POST de multipart/form-data, campo `image` em JPEG:
```json
{
  "items": [{"name":"arroz cozido","grams":120},{"name":"frango grelhado","grams":150}],
  "kcal": 430,
  "protein_g": 48,
  "confidence": "baixa"
}
```
O servidor deve autenticar o usuário, aplicar limite de requisições, proteger a chave da API no ambiente do servidor, configurar CORS exclusivamente para o domínio publicado, cuidar de consentimento e retenção de fotos, e retornar valores realistas com incerteza. **Nunca colocar chave do provedor no JavaScript, no GitHub público ou na configuração do usuário.** Estimativas por foto variam com óleo, molho, tamanho e modo de preparo; devem ser corrigidas pelo usuário.

## Notificações push no iPhone com app fechado — infraestrutura pendente
A service worker agora escuta eventos `push` e consegue exibir notificações recebidas. No entanto, a versão GitHub Pages é um host estático: não pode emitir push por conta própria. É necessário serviço backend com banco de assinaturas PushSubscription por usuário, chaves VAPID em segredo, rotina agendada, autorização e opt-in. No iOS o usuário precisa instalar como aplicativo pela Tela de Início e permitir notificações em gesto explícito. Até essa etapa, os avisos de água funcionam somente com o aplicativo aberto, conforme suporte do Safari.

## Privacidade
Histórico de saúde/consumo e fotos são armazenados localmente. Em uso normal, a foto não é enviada à internet. Somente com consentimento explícito, botão e endpoint próprio configurado pode haver upload. A migração para outra origem ou remoção dos dados do navegador pode apagar o histórico. Faça backups periódicos.
