package cn.ymotel.dactor.action.mybatis;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import org.apache.ibatis.session.SqlSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlQueryPageListActor implements Actor {
    private String countsql=null;
    private String datasql=null;
    private SqlSession sqlSession;
    private int size=10;
    @Override
    public Object Execute(Message message) throws Throwable {
        int ipage=message.getContextData("page",0);
        int isize=message.getContextData("size",size);
        Pageable pageable= PageRequest
                .of(ipage, isize);
        Map map =getParams(message,pageable);
        Page page=new PageImpl(getPageData(map),pageable,getTotal(map));
        return page;
    }
    public Map getParams(Message message, Pageable pageable){
        Map map = new HashMap();
        map.put("start", pageable.getOffset());
        map.put("size",pageable.getPageSize());
        return map;
    }
    public List getPageData(Map params){
       return  getSqlSession().selectList(datasql,params);
    }
    public long getTotal(Map params){
        return getSqlSession().selectOne(countsql,params);
    }

    public String getCountsql() {
        return countsql;
    }

    public void setCountsql(String countsql) {
        this.countsql = countsql;
    }

    public String getDatasql() {
        return datasql;
    }

    public void setDatasql(String datasql) {
        this.datasql = datasql;
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
