package cn.ldm.commons.utils.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Pageable {
	@JsonIgnore
	private Integer pageSize = 10;
	@JsonIgnore
	private Integer pageNo = 1;

	@JsonIgnore
	private Integer startIdx;

	@JsonIgnore
	private Integer endIdx;

	@JsonIgnore
	private Integer total;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getStartIdx() {
		return (pageNo - 1) * pageSize + 1;
	}

	public Integer getEndIdx() {
		return pageNo * pageSize > total ? total : pageNo * pageSize;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	@JsonIgnore
	public Integer getTotalPage() {// 总页数
		if (total == null || total == 0)
			return 0;
		return total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
	}

}
