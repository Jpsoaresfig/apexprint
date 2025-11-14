
# Custom Remote Print Server For Oracle Apex

Jasper 6.21.5: [https://github.com/umtigithub/apexprint]
Pentaho:


API que recebe **Report Query** do Oracle Apex e retorna o relatório customizado e dinâmico.

## Desenvolvedores

* **Tobias Freire**

  * Email: [tobiasfreire005@gmail.com](mailto:tobiasfreire005@gmail.com)
  * LinkedIn: [https://www.linkedin.com/in/tobias-freire](https://www.linkedin.com/in/tobias-freire)

* **Vinícius Soares**

  * Email: [vinisoares15@gmail.com](mailto:vinisoares15@gmail.com)
  * LinkedIn: [https://www.linkedin.com/in/vinicius-sf](https://www.linkedin.com/in/vinicius-sf/)
 
    * **João Pedro Figueiredo**

  * Email: [jpbusinesss@hotmail.com](mailto:jpbusinesss@hotmail.com)
  * LinkedIn: [https://www.linkedin.com/in/joaoPedroFigueiredo](linkedin.com/in/joãopedrosoaresfigueiredo)

---

## Oracle Apex - Info

O Oracle Apex permite a criação dos chamados **Report Queries**, que são basicamente resultados de queries convertidos para o formato XML e atrelados a um **Report Layout** que define o visual do relatório final.

Ao criar um **Report Query**, uma URL é gerada. Essa URL pode ser usada em **Ações Dinâmicas** atreladas a botões do Apex para gerar o relatório.

Para gerar o relatório, é importante que um **Remote Print Server** esteja registrado no WorkSpace.

* Acesse: **WorkSpace Utilities > Remote Servers**
* Crie um novo Remote Server com o endpoint:
  `/report/generate`

Durante os testes locais, recomendamos o uso do [ngrok](https://ngrok.com/) para expor a aplicação:

```bash
https://<ngrok-url>/report/generate
```

Demo:

```bash
https://apexprint.joaopessoa.pb.gov.br/report/generate
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
## Exemplo de Query

```query
select TNAME,TABTYPE,CLUSTERID from tab 
```

## Exemplo de Report Layout xmlData

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports
                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd"
              name="RelatorioBeneficios"
              pageWidth="595" pageHeight="842"
              columnWidth="535" leftMargin="30" rightMargin="30"
              topMargin="30" bottomMargin="30">

    <!-- Definição da fonte -->
    <style name="Base" isDefault="true" fontSize="10"/>
    
    <!-- Fields - correspondem aos campos do SQL -->
    <field name="TNAME" class="java.lang.String"/>
    <field name="TABTYPE" class="java.lang.String"/>
    <field name="CLUSTERID" class="java.lang.String"/>
    
    <!-- Título do relatório -->
    <title>
        <band height="50">
            <staticText>
                <reportElement x="0" y="0" width="535" height="30"/>
                <textElement textAlignment="Center" verticalAlignment="Middle">
                    <font size="16" isBold="true"/>
                </textElement>
                <text><![CDATA[Relatório de Benefícios]]></text>
            </staticText>
        </band>
    </title>

    <!-- Cabeçalho das colunas -->
    <columnHeader>
        <band height="20">
            <staticText>
                <reportElement x="0" y="0" width="100" height="20"/>
                <textElement verticalAlignment="Middle">
                    <font isBold="true"/>
                </textElement>
                <text><![CDATA[Nome da Tabela]]></text>
            </staticText>
            <staticText>
                <reportElement x="100" y="0" width="300" height="20"/>
                <textElement verticalAlignment="Middle">
                    <font isBold="true"/>
                </textElement>
                <text><![CDATA[Tipo de Tabela]]></text>
            </staticText>
            <staticText>
                <reportElement x="400" y="0" width="135" height="20"/>
                <textElement verticalAlignment="Middle">
                    <font isBold="true"/>
                </textElement>
                <text><![CDATA[Cluster ID]]></text>
            </staticText>
        </band>
    </columnHeader>

    <!-- Detalhes - dados do relatório -->
    <detail>
        <band height="20">
            <textField isBlankWhenNull="true">
                <reportElement x="0" y="0" width="100" height="20"/>
                <textElement verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[$F{TNAME}]]></textFieldExpression>
            </textField>
            <textField isBlankWhenNull="true">
                <reportElement x="100" y="0" width="300" height="20"/>
                <textElement verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[$F{TABTYPE}]]></textFieldExpression>
            </textField>
            <textField isBlankWhenNull="true">
                <reportElement x="400" y="0" width="135" height="20"/>
                <textElement verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[$F{CLUSTERID}]]></textFieldExpression>
            </textField>
        </band>
    </detail>

    <!-- Rodapé da página -->
    <pageFooter>
        <band height="30">
            <textField>
                <reportElement x="0" y="10" width="280" height="20"/>
                <textElement textAlignment="Left" verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA["Página " + $V{PAGE_NUMBER} + " de"]]></textFieldExpression>
            </textField>
            <textField evaluationTime="Report">
                <reportElement x="280" y="10" width="75" height="20"/>
                <textElement textAlignment="Left" verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
            </textField>
            <textField pattern="dd/MM/yyyy HH:mm:ss">
                <reportElement x="435" y="10" width="100" height="20"/>
                <textElement textAlignment="Right" verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[new java.util.Date()]]></textFieldExpression>
            </textField>
        </band>
    </pageFooter>

</jasperReport>
```


---

## API - Info

A aplicação é feita com **Spring Boot** e expõe um endpoint para receber o `templateFile` (.jrxml) e o `xmlData`.

### Serviços


* **GenerateReportFromJrxmlToPdf**
  Converte a string JRXML e a lista de dados em um relatório PDF com JasperReports.

### Controller

* **Endpoint base**: `/report`
* **Rota**: `/generate`
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
https://<ngrok-url>/report/generate
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
* [ ] Autenticação opcional com token para o endpoint
* [ ] Página de visualização prévia dos relatórios

---
