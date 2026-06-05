# GreenEden - Calculadora de Impacto Ambiental

## ✅ Correções Realizadas

### 1. **Warnings Removidos**
- ❌ Removido `PedidoService` não utilizado do `AutenticacaoController`
- ❌ Removidas variáveis não utilizadas do `PortalController`

### 2. **Gráficos**
- ✅ Gráficos agora aparecem PRIMEIRO (aba padrão ao abrir resultados)
- ✅ Todos os valores aparecem nas barras (incluindo zero)

### 3. **Fluxo de Compra**
- ✅ Página `/compra` agora funciona sem simulação prévia
- ✅ Mostra mensagem amigável se não houver impacto calculado

### 4. **Segurança de Senhas**
- ✅ Senhas agora precisam ter **MÍNIMO 8 caracteres**
- ✅ Exige: maiúsculas, minúsculas, números e caracteres especiais (!@#$%^&*)
- ✅ Interface de cadastro atualizada com dicas

### 5. **Login e Cadastro**
- ✅ Ambos centralizados e bem formatados
- ✅ Melhor UX com feedback de requisitos de senha

### 6. **Portal**
- ✅ Protegido por autenticação
- ✅ Redireciona para login se não autenticado
- ✅ Funciona após cadastro/login bem-sucedido

## 🚀 Como Executar

### Opção 1: Maven (Recomendado)
```bash
cd calculadora-sustentavel
mvn clean package
java -jar target/calculadora-sustentavel-0.0.1-SNAPSHOT.jar
```

### Opção 2: Script Windows
```bash
cd calculadora-sustentavel
.\run.bat
```

### Opção 3: IDE (Eclipse, IntelliJ)
1. Importe o projeto como Maven Project
2. Execute: `Run As > Spring Boot App`

## 📍 Acessar Aplicação

- **Homepage**: http://localhost:8080/
- **Calculadora**: http://localhost:8080/calculadora
- **Resultados**: http://localhost:8080/resultado
- **Planos**: http://localhost:8080/compra
- **Portal**: http://localhost:8080/portal (requer login)
- **Login**: http://localhost:8080/auth/login
- **Cadastro**: http://localhost:8080/auth/cadastro

## 🔐 Teste de Login

### Criar nova conta:
1. Clique em "Crie uma agora" no login
2. Preencha os dados (mínimo 8 caracteres, com maiúsculas, minúsculas, números e especiais)
3. Exemplo: `SenhaForte@123`

### Acessar Portal:
1. Faça login com suas credenciais
2. Acesse http://localhost:8080/portal
3. Visualize seu dashboard, pedidos, e configurações

## 🛠️ Stack Tecnológico

- **Backend**: Spring Boot 4.0.3
- **Database**: H2 (arquivo persistente em `./greenedendb`)
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js
- **Segurança**: Spring Security, BCrypt
- **Persistência**: JPA/Hibernate

## 📊 Características

✅ Calculadora de impacto ambiental
✅ Gráficos interativos com dados exibidos
✅ Planos de compra personalizados
✅ Portal do cliente
✅ Autenticação com senha forte
✅ Relatórios detalhados
✅ Banco de dados persistente

## 📝 Notas

- Senhas padrão para novo cadastro (se deixado em branco): `GreenEden@2024`
- Banco de dados armazenado em: `calculadora-sustentavel/greenedendb.mv.db`
- H2 Console disponível em: http://localhost:8080/h2-console (se necessário debug)
