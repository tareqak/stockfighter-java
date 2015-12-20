package gm.responses.abstractions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flash {
    private String info;
    private String success;
    private String warning;
    private String danger;

    public String getInfo() {
        return info;
    }

    @SuppressWarnings("unused")
    @JsonProperty("info")
    public void setInfo(String info) {
        this.info = info;
    }

    public String getSuccess() {
        return success;
    }

    @SuppressWarnings("unused")
    @JsonProperty("success")
    public void setSuccess(String success) {
        this.success = success;
    }

    public String getWarning() {
        return warning;
    }

    @SuppressWarnings("unused")
    @JsonProperty("warning")
    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getDanger() {
        return danger;
    }

    @SuppressWarnings("unused")
    @JsonProperty("danger")
    public void setDanger(String danger) {
        this.danger = danger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flash flash = (Flash) o;

        if (info != null ? !info.equals(flash.info) : flash.info != null)
            return false;
        if (success != null ? !success.equals(flash.success) :
                flash.success != null)
            return false;
        if (warning != null ? !warning.equals(flash.warning) :
                flash.warning != null)
            return false;
        return danger != null ? danger.equals(flash.danger) :
                flash.danger == null;

    }

    @Override
    public int hashCode() {
        int result = info != null ? info.hashCode() : 0;
        result = 31 * result + (success != null ? success.hashCode() : 0);
        result = 31 * result + (warning != null ? warning.hashCode() : 0);
        result = 31 * result + (danger != null ? danger.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (info != null) {
            stringBuilder.append("info=\"").append(info).append('\"');
        } else if (warning != null) {
            stringBuilder.append("warning=\"").append(warning).append('\"');
        } else if (success != null) {
            stringBuilder.append("success=\"").append(success).append('\"');
        } else if (danger != null) {
            stringBuilder.append("danger=\"").append(danger).append('\"');
        }
        return stringBuilder.toString();
    }
}
