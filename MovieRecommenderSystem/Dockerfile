# start by pulling the python image
FROM python:3.8

RUN pip install pipenv
# copy the requirements file into the image
COPY ./requirements.txt /app/requirements.txt



# switch working directory
WORKDIR /app

COPY ["movie_list.pkl","movie_recommender_model.pkl","./"]

# install the dependencies and packages in the requirements file
RUN pip3 install -r requirements.txt

# copy every content from the local file to the image
COPY ["app.py","./"]

EXPOSE 8082

# configure the container to run in an executed manner
ENTRYPOINT [ "python" ]

CMD ["app.py" ]