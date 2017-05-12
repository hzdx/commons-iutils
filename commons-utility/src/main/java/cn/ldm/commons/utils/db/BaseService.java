package cn.ldm.commons.utils.db;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * mybatis操作 base service
 */
public abstract class BaseService<T> {
	/**
	 * 默认的批量操作数量
	 */
	private static final int DEFAULT_BATCH_COUNT = 500;
	protected Class<T> entityClass;

	@Autowired
	private SqlSessionDaoSupport baseDao;

	/**
	 * <p>
	 * 获取queryDao
	 * </p>
	 */
	protected SqlSessionDaoSupport getQueryDao() {
		return this.baseDao;
	}

	@SuppressWarnings("unchecked")
	public BaseService() {
		entityClass = getSuperClassGenricType(getClass(), 0);
	}

	/**
	 *
	 */
	public Connection getConnection() {
		return getQueryDao().getSqlSession().getConnection();
	}

	public List<String> selectStringList(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().selectList(sqlId(sqlId), obj);
	}

	/**
	 * <p>
	 * 查询一个结果
	 * </p>
	 *
	 * @param sqlId
	 *            namespace，<br>
	 *            规则： T.sqlId，例如： Employee.findById <br>
	 *            你可以使用sqlId(String sqlId)方法自动添加泛型NameSpace T。<br>
	 * @param obj
	 *            参数
	 * @return 返回指定类型V
	 * @since: 0.0.1
	 */
	public <V> V selectOne(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().selectOne(sqlId(sqlId), obj);
	}

	/**
	 * 查询一个结果
	 */
	public <V> V selectOne(String sqlId) {
		return this.getQueryDao().getSqlSession().selectOne(sqlId(sqlId));
	}

	/**
	 * <p>
	 * 查询List结果
	 * </p>
	 */
	public <V> List<V> selectList(String sqlId) {
		return this.getQueryDao().getSqlSession().selectList(sqlId(sqlId));
	}

	/**
	 * <p>
	 * 查询List结果
	 * </p>
	 */
	public List<T> selectList(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().selectList(sqlId(sqlId), obj);
	}

	/**
	 * <p>
	 * 统计
	 * </p>
	 */
	public int count(String sqlId) {
		return this.selectOne(sqlId, null);
	}

	/**
	 * <p>
	 * 统计
	 * </p>
	 */
	public int count(String sqlId, Object obj) {
		return this.selectOne(sqlId(sqlId), obj);
	}

	/**
	 * 新增
	 *
	 * @return 是否成功
	 */
	public boolean insert(String sqlId) {
		return this.insert(sqlId, null);
	}

	/**
	 * 新增数据row
	 *
	 * @return 返回insert是否成功(影响行数>0)
	 */
	public boolean insert(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().insert(sqlId(sqlId), obj) > 0;
	}

	/**
	 * 插入行数据，并返回主键id
	 *
	 * @param obj
	 *            对象名称 mysql xml 返回主键写法 <br>
	 *            <selectKey resultType="java.lang.Integer" order="AFTER"
	 *            keyProperty="id" > <br>
	 *            SELECT LAST_INSERT_ID() AS ID<br>
	 *            </selectKey> <br>
	 * @return 主键id
	 */
	public int insertAndReturnId(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().insert(sqlId(sqlId), obj);
	}

	/**
	 * 更新行数据
	 *
	 * @return 更新是否成功（影响行数>0)
	 * @see public boolean update(String sqlId, Object obj)
	 */
	public boolean update(String sqlId) {
		return this.update(sqlId, null);
	}

	/**
	 * 更新行数据
	 *
	 * @return 更新是否成功（影响行数>0)
	 */
	public boolean update(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().update(sqlId(sqlId), obj) > 0;
	}

	/**
	 * 删除行数据
	 */
	public boolean delete(String sqlId) {
		return this.delete(sqlId, null);
	}

	/**
	 * 删除行数据
	 *
	 * @return 删除是否成功（影响行数>0)
	 */
	public boolean delete(String sqlId, Object obj) {
		return this.getQueryDao().getSqlSession().delete(sqlId(sqlId), obj) > 0;
	}

	/**
	 * 生成ibatis的sqlId namespace.sqlId
	 *
	 * @param sqlId
	 * @return
	 */
	protected String sqlId(String sqlId) {
		return sqlId.contains(".") ? sqlId : (entityClass.getSimpleName() + "." + sqlId);
	}

	/**
	 * 获取结果的map 集合
	 *
	 * @return Map<String,Object>
	 */
	public Map<String, Object> selectMap(String sqlId, Object obj, String key) {
		return this.getQueryDao().getSqlSession().selectMap(sqlId, obj, key);
	}

	/**
	 * 批量操作
	 *
	 * @param batchType
	 *            操作类型
	 * @return 批量操作（影响行数>0)
	 */
	public boolean batch(String sqlId, List<T> ts, OperationType batchType) {
		switch (batchType) {
		case insert:
			return this.insert(sqlId, ts);
		case update:
			return this.update(sqlId, ts);
		case delete:
			return this.delete(sqlId, ts);
		default:
			return false;
		}
	}

	/**
	 * 批量操作，默认500个一批
	 */
	@Transactional
	public void batchOperation(String sqlId, List<T> ts, OperationType batchType) throws Exception {
		this.batchOperation(sqlId, ts, batchType, DEFAULT_BATCH_COUNT);
	}

	/**
	 * 批量操作<br>
	 * 如果groupCount <=0,则使用默认分批数据
	 */
	@Transactional
	public void batchOperation(String sqlId, List<T> ts, OperationType batchType, int groupCount) throws Exception {
		// check parameter
		if (ts == null) {
			return;
		}
		int totalSize = ts.size();
		if (totalSize == 0) {
			return;
		}
		// 如果参数溢出或者 =0
		if (groupCount <= 0) {
			groupCount = DEFAULT_BATCH_COUNT;
		}
		// 共分多少批执行
		int batchs = totalSize % groupCount == 0 ? totalSize / groupCount : totalSize / groupCount + 1;
		// 分批执行
		for (int i = 0; i < batchs; i++) {
			// 分批起始坐标
			int startIndex = i * groupCount;
			// 分批结束坐标
			int endIndex = (i + 1) * groupCount;
			if (endIndex > totalSize) {
				endIndex = totalSize;
			}
			// 每批数据
			List<T> tmpList = new ArrayList<>(endIndex - startIndex);
			for (int j = startIndex; j < endIndex; j++) {
				// 获取员工对象
				tmpList.add(ts.get(j));
			}
			// 批量操作
			this.batch(sqlId, tmpList, batchType);
		}
	}

	/**
	 * 批量操作的类型
	 */
	public static enum OperationType {
		/**
		 * insert操作
		 */
		insert,
		/**
		 * update操作
		 */
		update,
		/**
		 * 删除操作
		 */
		delete,
		/**
		 * 清空表格数据
		 */
		truncate;
	}

	/**
	 * 获得超类
	 *
	 * @param clazz
	 * @param index
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(Class clazz, int index) {
		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}

	/**
	 * 分页查询样例
	 *  @Transactional
	 * public JsonResult list(TaskLog log) {
	 * 
	 * int count = super.selectOne("findCount", log);
	 * if (count < 1) {
	 * return JsonResult.OK.put("data", null);
	 * }
	 * log.setTotal(count);
	 * if (log.getPageNo() < 1) {
	 * log.setPageNo(1);
	 * }
	 * int totalPage = log.getTotalPage();
	 * if (log.getPageNo() > log.getTotalPage()) {
	 * log.setPageNo(totalPage);
	 * }
	 * List<TaskLog> logs = super.selectList("list", log);
	 * return JsonResult.OK.put("data", logs).put("total",
	 * count).put("totalPage", totalPage).put("pageNo",
	 * log.getPageNo());
	 * }
	 */
	
}
