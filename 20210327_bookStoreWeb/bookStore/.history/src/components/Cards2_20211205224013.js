import React from 'react';
import '../css/Cards.css';
import CardItem from './CardItem';
import { Button } from '../components/Button';
import axios from 'axios'
import Carousel from "./Carousel";
import Pagination from "./Pagination";

export default class Orders extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      book: [],
      recommendations: [],
      targetBookName: [],
      page: 1
    }
    this.updateInput = this.updateInput.bind(this);
    this.search = this.search.bind(this);
    this.searchType = this.searchType.bind(this);
    var query = window.location.href;
    console.log(query);
    console.log("props");
    console.log(props);
    this.setState({
      page: props.props
    })
    axios({
      method: 'GET',
      url: 'http://localhost:9091/books',
      params: {
        page: props.props
      }
    }).then(response => {
      const books = response.data;
      this.setState({
        book: books
      })
      console.log("getBook result:" + response.data);
      console.log(this.state.book);
    })
  }

  reset = () => {
    console.log("page");
    console.log(this.state.page);
    axios({
      method: 'GET',
      url: 'http://localhost:9091/books',
      params: {
        page: this.state.page
      }
    }).then(response => {
      const books = response.data;
      this.setState({
        book: books
      })
      console.log(this.state.book);
      this.render();
    })
  }


  updateInput(e) {
    console.log(e.target.value);
    this.state.targetBookName = e.target.value;
    console.log(this.state.targetBookName);
  }

  search() {
    axios({
      method: 'GET',
      url: 'http://localhost:9091/books',
      params: {
        query: this.state.targetBookName
      }
    }).then(response => {
      console.log("search result:");
      console.log(response.data);
      this.setState({ book: response.data });
      console.log(this.state.book);
    })
  }
  searchType() {
    axios({
      method: 'GET',
      url: 'http://localhost:9091/books',
      params: {
        recommendType: this.state.targetBookName
      }
    }).then(response => {
      console.log("search result:");
      console.log(response.data);
      this.setState({ recommendations: response.data });
      console.log(this.state.recommendations);
    })

    axios({
      method: 'GET',
      url: 'http://localhost:9091/books',
      params: {
        type: this.state.targetBookName
      }
    }).then(response => {

      this.setState({ book: response.data });
    })



  }
  renderCard(bookList, i) {
    return(<CardItem
      id={bookList[i].bookId}
      text={bookList[i].name}
      author={bookList[i].author}
      price={bookList[i].price}
      ISBN={bookList[i].isbn}
      src={bookList[i].image}
      inventory={bookList[i].inventory}
      label={bookList[i].type}
      path='/Book'
    />);
  }

  renderBooks() {
    if (!this.state.book) {
      alert("No matching book, please search again.");
      return <h1>No matching book, please search again.</h1>
    } else {
      return (<div className='cards__wrapper'>
        <ul className='cards__items'>
          {this.state.book.map((i) => (
            <CardItem
              id={i.bookId}
              text={i.name}
              author={i.author}
              price={i.price}
              ISBN={i.isbn}
              src={i.image}
              inventory={i.inventory}
              label={i.type}
              path='/Book'
            />
          )
          )}
          
        </ul>

        <Pagination />
      </div>);
    }
  }
  renderRecommendations() {
    var count = 0;
    if (!this.state.recommendations) {
      alert("No matching book, please search again.");
      return <h1>No matching book, please search again.</h1>
    } else {
      var newArr = [];
      console.log("new Array");
      console.log("recommendations");
      console.log(this.state.recommendations);
      // var newArr = new Array([element0[, element1[, …[, elementN]]]]);
      
      for(var i=0;i<4;i++)newArr.push(this.state.recommendations[i]);
      console.log("new Array");
      console.log(newArr);
      return (<div className='cards__wrapper'>
        <h1>recommendations</h1>
        <ul className='cards__items'>
        {this.state.recommendations.map((i) => (
            <CardItem
              id={i.bookId}
              text={i.name}
              author={i.author}
              price={i.price}
              ISBN={i.isbn}
              src={i.image}
              inventory={i.inventory}
              label={i.type}
              path='/Book'
            />
          )
          )}
        </ul>
      </div>);
    }
  }



  render() {
    // this.logRow();
    return (
      <div>
        <div className='cards__container'>
          <h1> Books</h1>
          <input type="text" onChange={this.updateInput} placeholder="search for book"></input>
          <div>
            Search for <Button buttonStyle='btn--blackBG2' onClick={this.search} > name</Button><Button onClick={this.searchType} className="">type</Button>
          </div>
          <Button onClick={this.reset}>Reset</Button>
          <Carousel />
          {this.renderBooks()}
          {this.renderRecommendations()}
        </div>
      </div>
    );
  }

}