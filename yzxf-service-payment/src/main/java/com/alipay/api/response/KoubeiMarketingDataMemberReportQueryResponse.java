package com.alipay.api.response;

import com.alipay.api.internal.mapping.ApiField;

import com.alipay.api.AlipayResponse;

/**
 * ALIPAY API: koubei.marketing.data.member.report.query response.
 * 
 * @author auto create
 * @since 1.0, 2016-05-28 22:41:49
 */
public class KoubeiMarketingDataMemberReportQueryResponse extends AlipayResponse {

	private static final long serialVersionUID = 6355722553464528712L;

	/** 
	 * 查询成功时返回json格式数据
	 */
	@ApiField("report_data")
	private String reportData;

	public void setReportData(String reportData) {
		this.reportData = reportData;
	}
	public String getReportData( ) {
		return this.reportData;
	}

}
