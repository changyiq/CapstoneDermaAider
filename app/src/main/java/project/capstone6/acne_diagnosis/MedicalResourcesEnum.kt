package project.capstone6.acne_diagnosis

enum class MedicalResourcesEnum (val website: String, val title: String) {
    AR1("https://www.healthlinkbc.ca/health-topics/hw199515", "HealthLinkBC - Acne"),
    AR2("https://www.healthlinkbc.ca/health-topics/tr5970", "HealthLinkBC - Rosacea"),
    AM("https://www.aad.org/public/diseases/skin-cancer/actinic-keratosis-overview", "American Academy of Dermatology - Actinic Keratosis"),
    AD("https://www.healthlinkbc.ca/health-topics/hw216104", "HealthLinkBC - Atopic Dermatitis"),
    BD("https://my.clevelandclinic.org/health/diseases/15855-bullous-pemphigoid", "Cleveland Clinic - Bullous Pemphigoid"),
    CI("https://www.mayoclinic.org/diseases-conditions/impetigo/symptoms-causes/syc-20352352", "Mayo Clinic - Impetigo"),
    EC("https://www.aboutkidshealth.ca/Article?contentid=773&language=English&utm_source=google-grant&utm_medium=cpc&utm_campaign=AKH%20Generic&utm_term=Dermatology%20Eczema&gclid=CjwKCAjw_L6LBhBbEiwA4c46uoPCu3WAajjGylKsgVTE-EVJuRuI89V00KamOljJCxn7kDBB945BjxoCG_MQAvD_BwE&gclsrc=aw.ds", "AboutKidsHealth - Eczama"),
    EDE("https://dermnetnz.org/topics/exanthems", "DermNet NZ - Exanthems"),
    HAIR("https://www.aad.org/public/diseases/hair-loss/types/alopecia", "American Academy of Dermatology - Alopecia Areata"),
    HPV("https://www.medicalnewstoday.com/articles/322674", "MedicalNewsToday - \"How are HPV and herpes different?\""),
    PI("https://my.clevelandclinic.org/health/articles/11014-pigmentation-abnormal-pigmentation", "Cleveland Clinic - Pigmentation: Abnormal Pigmentation"),
    CTD("https://lupuscanada.org/living-with-lupus/lupus-qa-ask-the-experts/?gclid=CjwKCAjw_L6LBhBbEiwA4c46uow7UoIWAW9dfPbRsQcf5E7hNWUYyubELaAvo_e3IUp1op_h4Ek-JRoCPf8QAvD_BwE", "Lupus Canada - \"Lupus Q&A: Ask the Experts\""),
    MM1("https://www.cancer.org/cancer/melanoma-skin-cancer/about/what-is-melanoma.html", "American Cancer Society - \"What Is Melanoma Skin Cancer?\""),
    MM2("https://my.clevelandclinic.org/health/diseases/4410-moles", "Cleveland Clinic - Moles"),
    NAIL("https://www.healthlinkbc.ca/health-topics/hw268101", "HealthLinkBC - Fungal Nail Infections"),
    CD("https://www.merckmanuals.com/en-ca/professional/dermatologic-disorders/dermatitis/contact-dermatitis", "Merck Manual - Contact Dermatitis"),
    PSO("https://www.healthlinkbc.ca/health-topics/hw58469", "HealthLinkBC - Psoriasis"),
    SLD("http://www.bccdc.ca/health-info/diseases-conditions/scabies", "BC Center for Disease Control - Scabies"),
    SK("https://www.healthlinkbc.ca/health-topics/tn8432", "HealthLinkBC - Seborrheic Keratoses"),
    SD("https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4753361/", "NCBI - Chronic inflammatory systemic diseases"),
    TRC("https://www.cdc.gov/fungal/diseases/ringworm/definition.html", "CDC - Fungal Disease"),
    UH("https://www.worldallergy.org/education-and-programs/education/allergic-disease-resource-center/professionals/urticaria", "WorldAllergy.ORG - Urticaria"),
    VT("https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4078200/", "NCBI - Vascular Tumors"),
    VP("https://www.healthlinkbc.ca/health-topics/abq3686", "HealthLinkBC - Learning About Vasculitis"),
    WM("https://www.healthlinkbc.ca/health-topics/aa18143", "HealthLinkBC - Molluscum Contagiosum")
}