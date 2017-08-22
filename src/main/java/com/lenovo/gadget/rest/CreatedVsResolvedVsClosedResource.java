package com.lenovo.gadget.rest;

import static com.atlassian.jira.permission.ProjectPermissions.BROWSE_PROJECTS;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.jfree.data.time.RegularTimePeriod;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.charts.Chart;
import com.atlassian.jira.charts.ChartFactory;
import com.atlassian.jira.charts.util.ChartUtils;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.statistics.StatusStatisticsMapper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.timezone.TimeZoneManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.ErrorCollection.Reason;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
import com.lenovo.gadget.rest.model.CustomIssue;
import com.lenovo.gadget.rest.model.JiraIssueReposnseObject;

import sun.java2d.pipe.AATextRenderer;

/**
 * A resource of message.
 */
@Path("/message")
public class CreatedVsResolvedVsClosedResource {
	
	protected static final String QUERY_STRING = "projectOrFilterId";
	public static final String DAYS_NAME = "daysprevious";
	private static final String PERIOD_NAME = "periodName";
	public static final String VERSION_LABEL = "versionLabel";
	private static final String IS_CUMULATIVE = "isCumulative";
	private static final String SHOW_UNRESOLVED_TREND = "showUnresolvedTrend";
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String NUM_CREATED_ISSUES = "numCreatedIssues";
	private static final String RETURN_DATA = "returnData";
	private static final String NUM_RESOLVED_ISSUES = "numResolvedIssues";
	private static final String NUM_CLOSED_ISSUES = "numClosedIssues";
	private static final String INLINE = "inline";
	private static final String FILTER_PREFIX = "filter-";
	private static final String PROJECT_PREFIX = "project-";
	private static final String JQL_PREFIX = "jql-";
	static final String PROJECT = "project";
	private static final String SEARCH_REQUEST = "searchRequest";
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	@Inject
	private ChartUtils chartUtils;
	@Inject
	private  ChartFactory chartFactory;
	@ComponentImport
	private ResourceDateValidator resourceDateValidator;
	@ComponentImport
	@Inject
	private  TimeZoneManager timeZoneManager;

	@ComponentImport
	@Inject
	protected  JiraAuthenticationContext authenticationContext;
	@ComponentImport
	@Inject
	protected  PermissionManager permissionManager;
	@ComponentImport
	@Inject
	private VelocityRequestContextFactory velocityRequestContextFactory;
	@ComponentImport
	@Inject
	protected  SearchService searchService;

	@Inject
	private ProjectManager projectManager;
	
	@Inject
	private ConstantsManager constantManager;
	
	
	
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage(@Context HttpServletRequest request,
			@QueryParam(QUERY_STRING) String queryString,
			@QueryParam(DAYS_NAME) @DefaultValue("30") final String days,
			@QueryParam(PERIOD_NAME) @DefaultValue("daily") final String periodName,
			@QueryParam(VERSION_LABEL) @DefaultValue("major") final String versionLabel,
			@QueryParam(IS_CUMULATIVE) @DefaultValue("true") final boolean isCumulative,
			@QueryParam(SHOW_UNRESOLVED_TREND) @DefaultValue("false") final boolean showUnresolvedTrend,
			@QueryParam(RETURN_DATA) @DefaultValue("false") final boolean returnData,
			@QueryParam(WIDTH) @DefaultValue("450") final int width,
			@QueryParam(HEIGHT) @DefaultValue("300") final int height,
			@QueryParam(INLINE) @DefaultValue("false") final boolean inline) throws Exception
    {
    	
		System.out.println(queryString);
    	if (StringUtils.isNotBlank(queryString) && !queryString.contains("-")) {
			queryString = "filter-" + queryString;
		}
		
		final Collection<ErrorCollection> errors = new ArrayList<ErrorCollection>();
		final ApplicationUser user = authenticationContext.getLoggedInUser();
		final SearchRequest searchRequest;
		final Map<String, Object> params = new HashMap<String, Object>();
		
		
		/*Project prjs = projectManager.getProjectByCurrentKey("TEST");
		System.out.println("Projects: " +  prjs.getId());*/
		
		searchRequest = getSearchRequestAndValidate(queryString, errors, params);
		/*final ChartFactory.PeriodName period = resourceDateValidator.validatePeriod(PERIOD_NAME, periodName, errors);
		final int numberOfDays = resourceDateValidator.validateDaysPrevious(DAYS_NAME, period, days, errors);

		if (!errors.isEmpty()) {
			return Response.status(400).entity(new CreatedVsResolvedVsClosedResourceModel("There was some problem")).build();
		}*/
		
		final ChartFactory.ChartContext context = new ChartFactory.ChartContext(user, searchRequest, width, height, inline);
		try{
			SearchResults searchResults =  searchService.search(user, searchRequest.getQuery(),PagerFilter.getUnlimitedFilter());
			//StringBuffer output =  new StringBuffer();
			JiraIssueReposnseObject issueReposnseObject = new JiraIssueReposnseObject();
			for(Issue issue: searchResults.getIssues()){
				if(issue.getAssigneeUser()!=null) {
					issueReposnseObject = processIssue(issueReposnseObject, issue);
					//output.append("\n"+issue.getSummary()  +" ::::::: " + issue.getCreated() +" ::::::: " + issue.getStatus().getName() +" ::::::: " + issue.getAssigneeUser().getDisplayName());
				}
			} 
			System.out.println(issueReposnseObject.toString());
			final Chart chart = chartFactory.generateCreatedVsResolvedChart(context, 10,ChartFactory.PeriodName.valueOf("daily") , ChartFactory.VersionLabel.valueOf("major"), isCumulative, showUnresolvedTrend);

			final Integer issuesCreated = (Integer) chart.getParameters().get(NUM_CREATED_ISSUES);
			final Integer issuesResolved = (Integer) chart.getParameters().get(NUM_RESOLVED_ISSUES);
			final Integer issuesClosed = (Integer) chart.getParameters().get(NUM_CLOSED_ISSUES);
			final String imageMap = chart.getImageMap();
			final String imageMapName = chart.getImageMapName();
			
			//TT6 ::::::: 2017-08-06 18:47:43.0 ::::::: Open ::::::: Deep Chandra Kholia 
			
			/*if (true) {
				final CategoryDataset completeDataset = (CategoryDataset) chart.getParameters().get("completeDataset");
				System.out.println("completeDataset.getColumnKeys() :::" +  completeDataset.getColumnKeys().toString());
				final XYDataset chartDataset = (XYDataset) chart.getParameters().get("chartDataset");
				final XYURLGenerator completeUrlGenerator = (XYURLGenerator) chart.getParameters().get("completeDatasetUrlGenerator");
				data = generateDataSet(completeDataset, completeUrlGenerator, chartDataset, showUnresolvedTrend);
			}*/
			
			DataRow[] data =   generateDataSet(searchResults.getIssues());
			getResolvedIssues(searchRequest.getQuery(), user);
			return Response.ok(new CreatedVsResolvedVsClosedResourceModel(issueReposnseObject)).build();
		}catch(Exception ex){
			throw ex;
		}
    }
	private JiraIssueReposnseObject processIssue(JiraIssueReposnseObject issueReposnseObject, Issue issue) throws ParseException {
		Map<String, ArrayList<CustomIssue>> issueMap = issueReposnseObject.getIssueMap();
		String theDate = simpleDateFormat.format(new Date(issue.getCreated().getTime()));
		ArrayList<CustomIssue> issueList = new ArrayList<CustomIssue>();
		if(issueMap.get(theDate)==null){
			issueList.add(new CustomIssue(issue.getStatus().getName(),1));
			issueMap.put(theDate,issueList);
		}else{
			issueList = issueMap.get(theDate);
			CustomIssue customIssue = new CustomIssue(issue.getStatus().getName(),0);
			if( issueList.contains(customIssue)){
				CustomIssue issue1 = issueList.get(issueList.indexOf(customIssue));
				issue1.setCount(issue1.getCount()+1);
				issueList.remove(customIssue);
				issueList.add(issue1);
				issueMap.put(theDate, issueList);
			}
			else{
				issueList.add(new CustomIssue(issue.getStatus().getName(),1));
				issueMap.put(theDate, issueList);
			}
		}
		return issueReposnseObject;
	}
	private ChartFactory.VersionLabel validateVersionLabel(String versionLabel, Collection<ErrorCollection> errors) {
		try {
			return ChartFactory.VersionLabel.valueOf(versionLabel);
		} catch (IllegalArgumentException e) {
			errors.add(new SimpleErrorCollection(VERSION_LABEL, Reason.valueOf("gadget.created.vs.resolved.invalid.version.label")));
		}
		return null;
	}
	
	private DataRow[] generateDataSet(List<Issue> issues){
		Instant instant = Instant.now();
		ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.of("US/Eastern")); 
		return null;
	}
	/*private DataRow[] generateDataSet(CategoryDataset dataset, XYURLGenerator urlGenerator, XYDataset chartdataset, boolean showTrend) {
		final TimePeriodUtils timePeriodUtils = new TimePeriodUtils(timeZoneManager);
		final DataRow[] data = new DataRow[dataset.getColumnCount()];

		// header
		for (int col = 0; col < dataset.getColumnCount(); col++) {
			Object key = dataset.getColumnKey(col);
			if (key instanceof RegularTimePeriod) {
				key = timePeriodUtils.prettyPrint((RegularTimePeriod) key);
			}

			int createdVal = dataset.getValue(0, col).intValue();
			String createdUrl = urlGenerator.generateURL(chartdataset, 0, col);
			int resolvedVal = dataset.getValue(1, col).intValue();
			String resolvedUrl = urlGenerator.generateURL(chartdataset, 1, col);
			Integer trendCount = null;
			if (showTrend) {
				trendCount = dataset.getValue(2, col).intValue();
			}
			data[col] = new DataRow(key, createdUrl, createdVal, resolvedUrl, resolvedVal, trendCount);
			System.out.println(data[col]);
		}

		return data;
	}*/

	protected SearchRequest getSearchRequestAndValidate(String queryString, Collection<ErrorCollection> errors, Map<String, Object> params) {
		SearchRequest searchRequest;
		if (StringUtils.isNotEmpty(queryString)) {
			params.put(QUERY_STRING, queryString);
			searchRequest = chartUtils.retrieveOrMakeSearchRequest(queryString, params);
			validateParams(errors, params);
			if (!errors.isEmpty()) {
				searchRequest = null;
			}
		} else {
			errors.add(new SimpleErrorCollection(QUERY_STRING,Reason.SERVER_ERROR));
			searchRequest = null;
		}
		return searchRequest;
	}

	private void validateParams(Collection<ErrorCollection> errors, Map<String, Object> params) {
		final String queryString = (String) params.get(QUERY_STRING);
		if (queryString.startsWith(FILTER_PREFIX)) {
			if (params.get(SEARCH_REQUEST) == null) {
				errors.add(new SimpleErrorCollection(QUERY_STRING,Reason.VALIDATION_FAILED));
			}
		} else if (queryString.startsWith(PROJECT_PREFIX)) {
			if (params.get(PROJECT) == null) {
				errors.add(new SimpleErrorCollection(QUERY_STRING,Reason.NOT_FOUND));
			} else {
				if (!permissionManager.hasPermission(BROWSE_PROJECTS, (Project) params.get(PROJECT), authenticationContext.getLoggedInUser())) {
					errors.add(new SimpleErrorCollection(QUERY_STRING,Reason.NOT_FOUND));
				}
			}
		} else if (queryString.startsWith(JQL_PREFIX)) {
			if (params.get(SEARCH_REQUEST) == null) {
				errors.add(new SimpleErrorCollection(QUERY_STRING,Reason.valueOf("dashboard.item.error.invalid.jql")));
			}
		} else {
			errors.add(new SimpleErrorCollection(QUERY_STRING,Reason.valueOf("dashboard.item.error.invalid.projectOrFilterId")));
		}
	}

	private Map<RegularTimePeriod, Number> getResolvedIssues(Query query, ApplicationUser remoteUser)
			throws IOException, SearchException {
		  final Map<RegularTimePeriod, Number> data = new TreeMap<RegularTimePeriod, Number>();
		  StatusStatisticsMapper resolvedMapper = new  StatusStatisticsMapper(constantManager);//DatePeriodStatisticsMapper(ChartUtil.getTimePeriodClass(periodName), DocumentConstants.ISSUE_STATUS, getTimeZone());
		 System.out.println("resolvedMapper :" + resolvedMapper);
		  // Collector hitCollector = new OneDimensionalObjectHitCollector(resolvedMapper, data, true);
		//searchProvider.search(query, remoteUser, hitCollector);
		return data;
	}
	
	  private TimeZone getTimeZone() {
	        return timeZoneManager.getLoggedInUserTimeZone();
	    }
}