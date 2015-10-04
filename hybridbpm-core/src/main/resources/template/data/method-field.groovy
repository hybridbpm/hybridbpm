    public $className get${(fieldName).capitalize()}() {
        return $fieldName;
    }

    public void set${(fieldName).capitalize()}($className $fieldName) {
        this.$fieldName = $fieldName;
    }