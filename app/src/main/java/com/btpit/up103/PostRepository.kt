package com.btpit.up103

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

interface PostRepository {
    fun getALL(): LiveData<List<Post>>
    fun removeById(id: Int)
    fun save(post: Post)
    fun likeById(id: Long)
    fun getAll(): LiveData<List<Post>>
}


class PostRepositoryInMemoryImpl2 : PostRepository {
    private var posts = listOf(
        Post(
            id = 2,
            autor = "БТПИТ",
            content = "hi world!",
            published = "18 сентября 10:12",
            likecount = 0,
            sharecount = 0,
            likedByMe = false

        ),
        Post(
            id = 1,
            autor = "БТПИТ",
            content = "Уэээээ!",
            published = "21 мая в 18:36",
            likecount = 0,
            sharecount = 0,
            likedByMe = false
        ),
    ).reversed()
    private val data = MutableLiveData(posts)

    override fun getALL(): LiveData<List<Post>> = data
    fun LikeById(id: Int) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe)
        }
        data.value = posts
    }
    override fun removeById(id: Int) {
        posts = posts.filter{it.id != id}
        data.value = posts
    }
    override fun likeById(id: Long) {
        val existingPosts = data.value.orEmpty().toMutableList()
        val index = existingPosts.indexOfFirst { it.id == id.toInt() }
        if (index != -1) {
            val post = existingPosts[index]
            existingPosts[index] = post.copy(
                likedByMe = !post.likedByMe,
                likecount = if (post.likedByMe) post.likecount - 1 else post.likecount + 1
            )
            save(existingPosts[index])
        }

    }
    override fun getAll(): LiveData<List<Post>> {
        TODO("Not yet implemented")
    }

    private var nextId1 = 1

    override fun save(post: Post) {
        val existingPosts = data.value.orEmpty().toMutableList()
        if(post.id == 0){
            val newPost = post.copy(id = nextId1++)
            existingPosts.add(0, newPost)
        }
        else{
            val index = existingPosts.indexOfFirst { it.id == post.id }
            if (index != -1) {
                existingPosts[index] = post
            }
        }
       data.value = existingPosts
      }




}
private val empty = Post(
    id = 0,
    autor = "",
    content = "",
    published = "",
    likecount = 0,
    sharecount = 0,
    likedByMe = false
)
class PostViewModel : ViewModel(){
    private val repository: PostRepository = PostRepositoryInMemoryImpl2()
    val data = repository.getALL()
    val edited = MutableLiveData(empty)

    fun save(){
        edited.value?.let{
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }
    fun changeContent(content: String) {
            val text = content.trim()
            if (edited.value?.content == text) {
                return
            }
            edited.value = edited.value?.copy(content = text)

    }

    fun likeById(id: Int) = repository.likeById(id.toLong())
    fun removeById(id: Int) = repository.removeById(id)


}




