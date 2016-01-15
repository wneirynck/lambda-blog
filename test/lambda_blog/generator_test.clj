(ns lambda-blog.generator-test
  (:refer-clojure :exclude [update])
  (:require [clojure.test :refer :all]
            [lambda-blog.generator :refer [update update-all whenever]]))

(deftest can-update-keys
  (let [ent {:key :value}]
    (is (= (update ent :key (constantly :new-value))
           {:key :new-value}))
    (update ent :key
            (fn [v]
              (is (= v :value))
              23)
            (fn [v]
              (is (= v 23))))))

(deftest can-update-collections
  (let [ent {:key [1 1 1]}]
    (is (= (update-all ent :key #(* 23 %))
           {:key [23 23 23]}))
    (update-all ent :key
                (fn [v]
                  (is (= v 1))
                  5)
                (fn [v]
                  (is (= v 5))))))

(deftest can-access-null-keys
  (is (= (update {} :key (constantly 23))
         {:key 23}))
  (is (= (update-all {} :key (constantly 23)) ;; NOTE There'se no key so there's no collection to update.
         {:key nil})))

(deftest update-preserves-collection-type
  (is (vector? (:v (update-all {:v [1 2 3]} :v (constantly 23)))))
  (is (set? (:s (update-all {:s #{1 2 3}} :s (constantly 5))))))

(deftest can-update-selected-entities
  (let [ent {:tags #{{:id :foo}
                     {:id :bar}
                     {:id :baz}}}]
    (is (= (update-all ent :tags
                       #(whenever % (fn [{:keys [id] :as t}]
                                      (= id :bar))
                                  (fn [t]
                                    (assoc t :hello :world))))
           {:tags #{{:id :foo}
                    {:id :bar :hello :world}
                    {:id :baz}}}))))
