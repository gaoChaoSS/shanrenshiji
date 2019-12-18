(function(angular, undefined) {
    // test data
    var tree_test = {
        modelName : 'common',
        entityName : 'Menu',
        name : '数的名称'
    };

    window.treeCtrl = function(actionPath, tree, $http) {
        // 动态构建新加数据行
        tree.actions = {
            // 关于 treeGridView
            clickTreeItem : function(item) {
                item.__selected = true;
                tree.__selecteTreeItem = item._id;
                // 其他动作用覆盖的方式，保持代码的简洁与独立
            },
            openTreeItem : function(item) {
                item.__isOpen = !item.__isOpen;
                if (item.__isOpen) {
                    $http.get(tree.actionPath + '/query?pageSize=500&_pid=' + item._id).success(function(re) {
                        item.items = re.content.items;
                    });
                }
            },
            selectRoot : function() {
                var root = tree.data == null || tree.data.length == 0 ? null : tree.data[0];
                if (root == null) {
                    return;
                }
                tree.actions.openTreeItem(root);
                tree.actions.clickTreeItem(root);
            }
        }

        return tree;
    }

})(angular);