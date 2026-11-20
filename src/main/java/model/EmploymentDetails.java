package model;

    public class EmploymentDetails {
        private String companyName;
        private String companyAddress;
        private String jobTitle;

        public EmploymentDetails(String companyName, String companyAddress, String jobTitle) {
            this.companyName = companyName;
            this.companyAddress = companyAddress;
            this.jobTitle = jobTitle;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyAddress() {
            return companyAddress;
        }

        public void setCompanyAddress(String companyAddress) {
            this.companyAddress = companyAddress;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        @Override
        public String toString() {
            return jobTitle + " at " + companyName + " (" + companyAddress + ")";
        }
    }
