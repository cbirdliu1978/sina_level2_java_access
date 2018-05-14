# sina_level2_java_access
sina level2数据的java接口。
本项目拟包含：
1. websocket实现，读取数据
2. chrome插件一个，为websocket提供auth串（插件周期性刷新获取最西南auth token）
3. 内嵌netty实现的webserver，以接收chrome 插件获取的auth token， 供websocket鉴权

注意：
	1. 由于本人已经放弃实时交易信号判断，不用比较久了，sina是否修改代码、能否跑通不知，如果跑不通可联系4472012#a#qq.com
	2. 如跑不通则仅仅提供一个思路
	3. sina level2数据是收费的说
	