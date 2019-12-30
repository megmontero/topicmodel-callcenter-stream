# topicmodel-callcenter-stream
Servicios de Kafka Streams para el TFM modelización de temas de llamadas en tiempo real. El código aquí 
presentado forma el *core* de la capa de *streaming* del proyecto. 

La documentación completa del TFM está accesible en https://github.com/...

Los servicios contenidos en este código son: 

## Tokenizer
Este microservicio tiene como entrada las llamadas transcritas que llegan al bus en tiempo real. A 
partir del texto de las mismas el sistema inicia un proceso de *tokenizacion* eliminando *stopwords*, 
carácteres especiales (signos de puntuación, 'ñ's, acentos...), palabras comunes, 
números, nombres propios y pasando a minúscula cada uno de los tokens.  El 
proceso de *tokenización* pretende ser lo más fiel posible al proceso realizado en Python durante 
la etapa de entrenamiento del modelo.

Una vez obtenida la lista de \textit{tokens} esta se vuelve a disponibilizar en el bus en un nuevo \textit{topic}. Este nuevo \textit{topic} con las llamadas \textit{tokenizadas} puede ser de utilidad no solo para nuestro sistema, si no también para cualquier otro sistema de análisis de texto que necesite partir de los datos \textit{tokenizados}.

El \textit{topic} de entrada del microservicio  *Tokenizer*, llamado *CALLS*, tendrá 
como clave el código de la llamada y como cuerpo un objeto *json* con los siguientes campos:


-  **co_verint** : Código de la llamada. 
-  **call_text** : Texto de la llamada. 
-  **call_timestamp**: Hora de la llamada.
-  **province**: Provincia desde la que se ha realizado la llamada. 
-  **co\_province**: Código de provincia desde la que se ha realizado la llamada. 
-  **duration**: Duración en segundos de la llamada. 
-  **control_type**: De existir, tipo de llamada (usado para el proceso de verificación). 
 

Como salida escribirá en un *topic*  llamado *TOKENS.CALLS* un evento cuya clave será el código 
de la llamada y cuyo cuerpo un objeto *json* con los siguientes campos:

- **co_verint** : Código de la llamada. 
- **call_text** : Texto de la llamada. 
- **call_timestamp**: Hora de la llamada.
- **province**: Provincia desde la que se ha realizado la llamada. 
- **co_province**: Código de provincia desde la que se ha realizado la llamada. 
- **duration**: Duración en segundos de la llamada. 
- **control_type**: De existir, tipo de llamada (usado para el proceso de verificación).  
- **tokens**: Lista de tokens extraída de *call_text*. 

## Sequencer

## Predicter

