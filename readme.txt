1��mvn clean install compile -Dmaven.test.skip=true

2����target\elasticsearch-taste-0.0.1-SNAPSHOT.jar��target\classes\plugin-descriptor.properties�ļ����Ƶ��½���elasticsearch�ļ��У�λ�����⣩

3�����elasticsearch�ļ��е�ѹ���ļ����ļ���Ϊtaste-5.4.0.1.zip

4������elasticsearch-5.4.1\bin�ļ�����
4.1�����֮ǰ��װ��ͬ��plugin����Ҫж�أ�ִ�У�elasticsearch-plugin remove taste
4.2����װ�²����ִ�У�elasticsearch-plugin install file:{path}/taste-5.4.0.1.zip ������{path}�Ǹ�ѹ�����ľ���·����

PS�����Ĳ������󣬻�õ�һ��elasticsearch-5.4.1\plugins\taste�ļ��У������Ҫ��Դ�������������ֻ�轫���ļ��и��Ƶ�Դ���elasticsearch-5.4.1\core\src\main\plugins\taste\