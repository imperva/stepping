package algos.etlalgo.dto;

import java.util.Objects;

public class EtlTuple {

    private String osUser;
    private String srcIp;
    private String srcHost;
    private String srcApp;
    private String destIp;
    private String destHost;
    private String database;
    private String dbUser;
    private String serviceType;
    private String customerId;

    public String getOsUser() {
        return osUser;
    }

    public void setOsUser(String osUser) {
        this.osUser = osUser;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public String getSrcApp() {
        return srcApp;
    }

    public void setSrcApp(String srcApp) {
        this.srcApp = srcApp;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getDestHost() {
        return destHost;
    }

    public void setDestHost(String destHost) {
        this.destHost = destHost;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EtlTuple)) return false;
        EtlTuple etlTuple = (EtlTuple) o;
        return Objects.equals(osUser, etlTuple.osUser) &&
                Objects.equals(srcIp, etlTuple.srcIp) &&
                Objects.equals(srcHost, etlTuple.srcHost) &&
                Objects.equals(srcApp, etlTuple.srcApp) &&
                Objects.equals(destIp, etlTuple.destIp) &&
                Objects.equals(destHost, etlTuple.destHost) &&
                Objects.equals(database, etlTuple.database) &&
                Objects.equals(dbUser, etlTuple.dbUser) &&
                Objects.equals(serviceType, etlTuple.serviceType) &&
                Objects.equals(customerId, etlTuple.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(osUser, srcIp, srcHost, srcApp, destIp, destHost, database, dbUser, serviceType, customerId);
    }
}
