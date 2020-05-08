package cn.ymotel.dactor.action.mybatis;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.sequence.IdWorker;
import org.apache.ibatis.session.SqlSession;

import java.util.Map;

public class SqlUpdateActor implements Actor {
    private SqlSession sqlSession;
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     *
     * @param message
     * @return 返回成功执行的条数
     * @throws Exception
     */
    @Override
    public Object Execute(Message message) throws Throwable {

        return this.getSqlSession().update(sql,getParams(message));
    }

    public Map getParams(Message message){
        Map params=message.getContext();
        params.put("_seq", IdWorker.getInstance().nextId());
        params.put("_transdate", new java.sql.Date(System.currentTimeMillis()));
        params.put("_transtime", new java.sql.Timestamp(System.currentTimeMillis()));
        return params;
    }
}
