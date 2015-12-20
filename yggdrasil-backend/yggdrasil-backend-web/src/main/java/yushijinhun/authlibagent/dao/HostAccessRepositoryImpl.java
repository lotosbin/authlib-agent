package yushijinhun.authlibagent.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.api.AccessPolicy;
import yushijinhun.authlibagent.dao.pojo.AccessPolicyDao;

@Repository("host_access_repository")
@Transactional
public class HostAccessRepositoryImpl implements HostAccessRepository {

	@Qualifier("session_factory")
	private SessionFactory database;

	@Override
	@Transactional(readOnly = true)
	public AccessPolicy getPolicy(String host) {
		Session session = database.getCurrentSession();
		AccessPolicyDao policy = session.get(AccessPolicyDao.class, host);
		return policy == null ? null : policy.getPolicy();
	}

	@Override
	public void setPolicy(String host, AccessPolicy policy) {
		Session session = database.getCurrentSession();
		AccessPolicyDao policyDao = session.get(AccessPolicyDao.class, host);
		if (policy == null && policyDao != null) {
			session.delete(policyDao);
		} else if (policy != null && policyDao == null) {
			AccessPolicyDao newPolicy = new AccessPolicyDao();
			newPolicy.setHost(host);
			newPolicy.setPolicy(policy);
			session.save(newPolicy);
		} else if (policy != null && policyDao != null) {
			policyDao.setPolicy(policy);
			session.update(policyDao);
		}
	}

}
