package com.goblin.trade.sell.download.level2.sina;

import java.io.IOException;
import java.util.List;

public interface WebSocketFactory {

	List<String> getWatchCodes();

	SinaLevel2Fetcher newFetcher() throws IOException;

}
