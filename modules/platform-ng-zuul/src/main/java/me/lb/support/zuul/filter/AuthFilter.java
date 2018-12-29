package me.lb.support.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class AuthFilter extends ZuulFilter {

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.getRequest();
		// TODO: 这里可以进行权限控制，具体怎么写没想好
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	/**
	 * to classify a filter by type. Standard types in Zuul are "pre" for pre-routing filtering,
	 * "route" for routing to an origin, "post" for post-routing filters, "error" for error handling.
	 * We also support a "static" type for static responses see StaticResponseFilter.
	 * Any filterType made be created or added and run by calling FilterProcessor.runFilters(type)
	 */
	@Override
	public String filterType() {
		return "pre";
	}

}
