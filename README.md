
# Custom Remote Print Server For Oracle Apex

API que recebe **Report Query** do Oracle Apex e retorna o relatório customizado e dinâmico.

## Desenvolvedores

* **Tobias Freire**

  * Email: [tobiasfreire005@gmail.com](mailto:tobiasfreire005@gmail.com)
  * LinkedIn: [https://www.linkedin.com/in/tobias-freire](https://www.linkedin.com/in/tobias-freire)

* **Vinícius Soares**

  * Email: [vinisoares15@gmail.com](mailto:vinisoares15@gmail.com)
  * LinkedIn: [https://www.linkedin.com/in/vinicius-sf](https://www.linkedin.com/in/vinicius-sf/)

---

## Oracle Apex - Info

O Oracle Apex permite a criação dos chamados **Report Queries**, que são basicamente resultados de queries convertidos para o formato XML e atrelados a um **Report Layout** que define o visual do relatório final.

Ao criar um **Report Query**, uma URL é gerada. Essa URL pode ser usada em **Ações Dinâmicas** atreladas a botões do Apex para gerar o relatório.

Para gerar o relatório, é importante que um **Remote Print Server** esteja registrado no WorkSpace.

* Acesse: **WorkSpace Utilities > Remote Servers**
* Crie um novo Remote Server com o endpoint:
  `/api/apex/generatePdfFromApex`

Durante os testes locais, recomendamos o uso do [ngrok](https://ngrok.com/) para expor a aplicação:

```bash
https://<ngrok-url>/api/apex/generatePdfFromApex
```

> ℹ️ O ngrok gera URLs efêmeras. Sempre copie a nova URL após iniciar o ngrok.
> Para URLs estáticas, veja a [documentação do ngrok](https://ngrok.com/).

---

## ⚙️ Criando a Ação Dinâmica no Apex

Para gerar o relatório via botão no Apex:

1. Crie uma **página** com um botão (por exemplo, "Gerar PDF").
2. Crie uma **Ação Dinâmica** associada a esse botão.
3. Na ação, selecione o tipo: **Print Report**.
4. Em **Settings > Report Query**, selecione a **Report Query** criada anteriormente.

> ⚠️ **Importante:**

* Vá em: **Shared Components > Report Queries**
* Edite a Report Query desejada
* Em **Report Layout**, selecione o layout criado anteriormente.

> O layout pode ser criado com os valores padrão, **exceto pelo campo Page Template**, que deve ser definido com o conteúdo em formato XML conforme exemplo em `xmlData` e poderá ser alterado e customizado de acordo com o report que
você deseja criar.

---

## Exemplo de xmlData

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE jasperReport PUBLIC "-//JasperReports//DTD Report Design//EN"
"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd">
<jasperReport name="sample" pageWidth="595" pageHeight="842" columnWidth="555"
              leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20">
    <detail>
        <band height="20">
            <staticText>
                <reportElement x="0" y="0" width="200" height="20"/>
                <text><![CDATA[Hello Jasper!]]></text>
            </staticText>
        </band>
    </detail>
</jasperReport>
```
> ❌ Atualmente, a aplicação **não suporta múltiplas queries** dentro de um único Report Query.

---

## API - Info

A aplicação é feita com **Spring Boot** e expõe um endpoint para receber o `templateFile` (.jrxml) e o `xmlData`.

### Serviços

* **XmlToJasperDataSource**
  Converte o XML em uma lista de mapas (`List<Map<String, Object>>`)

* **GenerateReportFromJrxmlToPdf**
  Converte a string JRXML e a lista de dados em um relatório PDF com JasperReports.

### Controller

* **Endpoint base**: `/api/jasper`
* **Rota**: `/generatePdfFromJasper`
  Recebe uma requisição `multipart/form-data` com `xmlData` e `templateFile`
  Retorna um PDF como resposta.

### Swagger

* Acesse a documentação Swagger:
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Passo a passo

### Forma 1: Local com ngrok

**Pré-requisitos**: Java 17, Maven e ngrok instalados.

1. Rodar aplicação:

```bash
mvn spring-boot:run
```

2. Rodar ngrok:

```bash
ngrok http 8080
```

3. Copiar URL `Forwarding` do ngrok e configurar no Apex:

```bash
https://<ngrok-url>/api/apex/generatePdfFromApex
```

4. Testar:

   * Criar página de teste no Apex com botão + ação dinâmica Print Report

---

### Forma 2: Docker

**Pré-requisitos**: Docker instalado

1. Criar imagem Docker:

```bash
docker build -t printserver .
```

2. Rodar container:

```bash
docker run -p 8080:8080 printserver
```

3. Siga os passos 2 a 4 da Forma 1.

---

## TO-DO

* [ ] Permitir exportação para outros formatos (XLSX, DOCX, etc.)
* [ ] Suporte para múltiplas queries no Report Query do Apex
* [ ] Autenticação opcional com token para o endpoint
* [ ] Página de visualização prévia dos relatórios

---
