package gm.responses.abstractions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flash {
    private String info;
    private String success;
    private String warning;
    private String danger;
    private String error;

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

    public String getError() {
        return error;
    }

    @SuppressWarnings("unused")
    @JsonProperty("error")
    public void setError(final String error) {
        this.error = error;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Flash f = (Flash) o;

        if (info != null ? !info.equals(f.info) : f.info != null)
            return false;
        if (success != null ? !success.equals(f.success) : f.success != null)
            return false;
        if (warning != null ? !warning.equals(f.warning) : f.warning != null)
            return false;
        if (danger != null ? !danger.equals(f.danger) : f.danger != null)
            return false;
        return error != null ? error.equals(f.error) : f.error == null;

    }

    @Override
    public int hashCode() {
        int result = info != null ? info.hashCode() : 0;
        result = 31 * result + (success != null ? success.hashCode() : 0);
        result = 31 * result + (warning != null ? warning.hashCode() : 0);
        result = 31 * result + (danger != null ? danger.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
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
        } else if (error != null) {
            stringBuilder.append("error=\"").append(error).append('\"');
        }
        return stringBuilder.toString();
    }
}
