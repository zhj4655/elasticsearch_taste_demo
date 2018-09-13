1、mvn clean install compile -Dmaven.test.skip=true

2、将target\elasticsearch-taste-0.0.1-SNAPSHOT.jar和target\classes\plugin-descriptor.properties文件复制到新建的elasticsearch文件夹（位置随意）

3、添加elasticsearch文件夹到压缩文件，文件名为taste-5.4.0.1.zip

4、进入elasticsearch-5.4.1\bin文件夹下
4.1、如果之前安装过同名plugin，需要卸载，执行：elasticsearch-plugin remove taste
4.2、安装新插件，执行：elasticsearch-plugin install file:{path}/taste-5.4.0.1.zip （其中{path}是该压缩包的绝对路径）

PS：第四步结束后，会得到一个elasticsearch-5.4.1\plugins\taste文件夹，如果需要在源码中启动插件，只需将该文件夹复制到源码版elasticsearch-5.4.1\core\src\main\plugins\taste\