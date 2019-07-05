# PageRankSequencial
Algoritmo de PageRank sequencial

Para executar o programa é necessário que tenha as seguintes dependencias instaladas

Dependencias:

    -> Java
        $ sudo apt-get default-jdk 

Após instalar as dependencias é necessário compilar o programa, para isso use o comando na pasta raiz do projeto

    $ javac pagerank.java

Se tudo estiver correto o programa deverá compilar e um arquivo .class será gerado.
Agora basta executar a aplicação:

    $ java pagerank

Resultado esperado (Arquivo pagerank_output.txt):

    Top 10 URLs 

    --------------------------------------
    |       URL             |       Page Rank               |
    --------------------------------------
    |       2               |       0,21593238769786754     |
    ---------------------------------------
    |       1               |       0,17547258618427736     |
    ---------------------------------------
    |       4               |       0,02792357412002029     |
    ---------------------------------------
    |       38              |       0,02710376614838421     |
    ---------------------------------------
    |       0               |       0,01375386305192000     |
    ---------------------------------------
    |       14              |       0,00890593236193573     |
    ---------------------------------------
    |       22              |       0,00780884977343809     |
    ---------------------------------------
    |       170             |       0,00542074016667799     |
    ---------------------------------------
    |       862             |       0,00479513239577700     |
    ---------------------------------------
    |       86690           |       0,00479291539511509     |
    ---------------------------------------

*Obs: Altere o valor das variáveis inputfilename, outputfilename e iteration para alterar o arquivo de entrada, arquivo de saída e quantidade de iterações, respectivamente.