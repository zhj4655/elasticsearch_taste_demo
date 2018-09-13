package com.taste.elasticsearch_taste.rest;

import static com.taste.elasticsearch_taste.action.LoggerUtils.emitErrorResponse;
import static org.elasticsearch.rest.RestRequest.Method.POST;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.action.search.RestSearchAction;
import org.elasticsearch.search.SearchHits;

import com.taste.elasticsearch_taste.action.TasteEventAction;
import com.taste.elasticsearch_taste.common.TasteEventRequestBuilder;
import com.taste.elasticsearch_taste.common.TasteEventResponse;

public class TasteEventRestAction extends BaseRestHandler{
    public ArgumentParser argumentParser;

    @Inject
    public TasteEventRestAction(final Settings settings,final RestController restController) {
        super(settings);
        restController.registerHandler(RestRequest.Method.GET, "/_taste/{action}", this);
        restController.registerHandler(RestRequest.Method.GET, "/_taste", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        if (request.method() == POST && !request.hasContent()) {
            return channel -> emitErrorResponse(channel, logger, new IllegalArgumentException("Request body was expected for a POST request."));
        }
        String action = request.param("action");
        if (action != null) {
            logger.info("action: {}", action);
            return createActionResponse(request, client);
        } else {
            logger.info("action: null");
            return createNoActionResponse(request);
        }
    }

    // 一、对应URL为 /_taste/{action}
    private RestChannelConsumer createActionResponse(RestRequest request, NodeClient client){
        this.argumentParser= new ArgumentParser(request);

        if(this.argumentParser.actionCode.equals(ArgumentParser.ACTION_ZERO)){
            logger.info("DoNothing");
            return channel -> {
                XContentBuilder builder = channel.newBuilder();
                builder.startObject();
                builder.field("res","DoNothing -- ACTION_ZERO");
                builder.endObject();
                channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
            };
        }
        else{
            // 1、通过argumentParser进行数据查询，构造自定义的request请求
            final TasteEventRequestBuilder actionBuilder=new TasteEventRequestBuilder(client);
            SearchRequestBuilder requestBuilder = client.prepareSearch(argumentParser.index).setQuery(QueryBuilders.matchAllQuery())
                    .setFrom(argumentParser.indexFrom).setSize(argumentParser.indexSize).setExplain(true);
            SearchRequest searchRequest=new SearchRequest();
            try {
                RestSearchAction.parseSearchRequest(searchRequest, request, null);
            } catch (IOException e1) {
                logger.debug("Failed to emit response.", e1);
                e1.printStackTrace();
            }
            actionBuilder.setSearchRequest(requestBuilder);
            return channel -> client.execute(TasteEventAction.INSTANSE, actionBuilder.request(),new ActionListener<TasteEventResponse>() {
                @Override
                public void onResponse(TasteEventResponse response) {
                    try{
                        // 2、获得查询数据
                        SearchResponse searchResponse = response.getSearchResponse();
                        SearchHits searchHits = searchResponse.getHits();
                        XContentBuilder builder = channel.newBuilder();
                        builder.startObject();

                        for(int i=argumentParser.indexFrom; i<argumentParser.indexFrom+argumentParser.indexSize; i++) {
                            Map<String, Object> map = searchHits.getHits()[i].getSource();
                            builder.field(String.valueOf(i), map.toString());
                        }

                        builder.endObject();
                        channel.sendResponse( new BytesRestResponse(RestStatus.OK, builder));
                    }catch(Exception e){
                        logger.debug("Failed to emit response.", e);
                        onFailure(e);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    emitErrorResponse(channel, logger, e);
                }

            });
        }
    }


    // 2、对应URL为 /_taste
    private RestChannelConsumer createNoActionResponse(RestRequest request) {

        return channel -> {
            Message message = new Message();
            XContentBuilder builder = channel.newBuilder();
            builder.startObject();
            message.toXContent(builder, request);
            builder.endObject();
            channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
        };
    }

    class Message implements ToXContent {
        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            // http://127.0.0.1:9200/_taste
            return builder.field("res", "This is ES plugin demo");
        }
    }
}

// http://127.0.0.1:9200/_taste/parameter?actionCode=00
// http://127.0.0.1:9200/_taste/parameter?actionCode=01&index=xxx&indexFrom=0&indexSize=10
class ArgumentParser {
    public static final String ACTION_ZERO = "00";
    public static final String ACTION_ONE = "01";

    public String actionCode;
    public String index;
    public int indexFrom;
    public int indexSize;

    public ArgumentParser(RestRequest request) {
        actionCode = request.param("actionCode");
        if (actionCode.equals(ACTION_ZERO)) {

        } else if (actionCode.equals(ACTION_ONE)) {
            index = request.param("index");
            indexFrom = Integer.parseInt(request.param("indexFrom"));
            indexSize = Integer.parseInt(request.param("indexSize"));
        }
    }

}