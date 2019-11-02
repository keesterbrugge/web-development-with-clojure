(ns html-templating.core
  (:require [selmer.parser :as selmer]
            [selmer.filters :as filters]))

(selmer/render "hello, {{name}}" {:name "keesterbrugge"})

(selmer/render-file "hello.html" {:name "kees' world"})


(selmer/render-file "hello.html" {:items (range 10)})

(filters/add-filter! :empty? empty?)

(selmer/render "{% if files|empty? %} no files {% else %} files {% endif %}"
               {:files ["blah"]})



(filters/add-filter! :foo (fn [x] [:safe (.toUpperCase x)]))


(filters/add-filter! :foo2 (fn [x] (.toUpperCase x)))

(selmer/render "{{x|foo}}" {:x "<div> I'm safe </div>"})


(selmer/render "{{x|foo2}}" {:x "<div> I'm safe </div>"})

